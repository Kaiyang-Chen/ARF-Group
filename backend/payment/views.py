import json
import uuid

from django.http import HttpRequest, JsonResponse, HttpResponse
from browser.models import ProductInfo
from django.views.decorators.csrf import csrf_exempt
from payment.models import Trade
from alipay import AliPay
from usersystem.models import UserProfile


@csrf_exempt
def buy_product(request: HttpRequest):
    '''
    send: {"UID":"xxxxx"}
    '''
    user = request.user
    if request.method == "POST":
        if not user.is_authenticated:
            return JsonResponse({"msg": "login first"})
        try:
            json_content = json.loads(request.body)
            UID = uuid.UUID(json_content["UID"])
            prod = ProductInfo.objects.get(UID=UID)
            if prod.sold_state:
                return JsonResponse({"msg": "failed: product sold"})
            price = prod.price
            userinfo = UserProfile.objects.get(user=user)
            if price <= userinfo.money:
                Trade.objects.create(seller=prod.owner,
                                     buyer=user, done=True, product=prod)
                userinfo.money = userinfo.money-price
                prod.sold_state = True
                prod.save()
                userinfo.save()
                return JsonResponse({"msg": "success: enough money in your account"})
            else:
                with open("app_private_key.pem") as f:
                    app_private_key_string = f.read()
                with open("alipay_public_key.pem") as f:
                    alipay_public_key_string = f.read()
                record = Trade.objects.create(seller=prod.owner,
                                              buyer=user, done=False, product=prod)
                alipay = AliPay(
                    appid="2021000121633375",
                    app_notify_url=None,
                    app_private_key_string=app_private_key_string,
                    alipay_public_key_string=alipay_public_key_string,
                    debug=True
                )
                res = alipay.api_alipay_trade_page_pay(
                    out_trade_no=str(record.out_trade_no),
                    total_amount=float(record.product.price),
                    subject=str(record.product.name),
                    return_url=None,
                    notify_url="https://101.132.97.115/pay_notify/",
                )
                gataway = 'https://openapi.alipaydev.com/gateway.do?'
                return JsonResponse({"url": gataway+res})
        except Exception as e:
            return JsonResponse({"msg": "failed: wrong UID", "e": str(e)})
    return JsonResponse({"msg": "should use POST method"})


@csrf_exempt
def pay_notify(request: HttpRequest):
    try:
        json_content = json.loads(request.body)
        out_trade_no = json_content["out_trade_no"]
        status = json_content["trade_status"]
        if status in ("TRADE_SUCCESS", "TRADE_FINISHED"):
            trade = Trade.objects.get(out_trade_no=uuid.UUID(out_trade_no))
            trade.done = True
            trade.save()

            price = trade.product.price
            sellerinfo = UserProfile.objects.get(trade.seller)
            sellerinfo.money = sellerinfo.money+price
            sellerinfo.save()

            trade.product.sold_state = True
            trade.product.save()
            return HttpResponse("success")
    except:
        pass
    return HttpResponse('failed')
