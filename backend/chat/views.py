import json
import uuid

from django.contrib.auth.models import User
from django.core.files.storage import FileSystemStorage
from django.http import HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt

from chat.models import ChatMessages


@csrf_exempt
def post_chat(request: HttpRequest):
    '''
    send: {"seller":"wxy", "content":"hello"}
    '''
    user = request.user
    if request.method == "POST":
        if not user.is_authenticated:
            return JsonResponse({"msg": "login first"})
        username = user.username
        try:
            json_content = json.loads(request.body)
            if "seller" in json_content.keys():
                seller = json_content["seller"]
                buyer = username
            else:
                buyer = json_content["buyer"]
                seller = username
            buyer = User.objects.get(username=buyer)
            seller = User.objects.get(username=seller)
            content = json_content["content"]
            assert content != "" and len(content) < 256
        except:
            return JsonResponse({"msg": "wrong massege"})
        ChatMessages.objects.create(
            seller=seller, buyer=buyer, content=content, is_picture=False)
        return JsonResponse({})
    return JsonResponse({"msg": "failed"})


@csrf_exempt
def post_chat_picture(request: HttpRequest):
    '''
    send: {"buyer":"wxy"} and some picture
    '''
    if not request.user.is_authenticated:
        return JsonResponse({"msg": "login first"})
    if request.method == "POST":
        try:
            seller_get = request.POST.get("seller")
            buyer_get = request.POST.get("buyer")
            assert seller_get is None or buyer_get is None
            assert seller_get is not None or buyer_get is not None
            seller = seller_get if seller_get is not None else request.user.username
            buyer = buyer_get if buyer_get is not None else request.user.username
            seller = User.objects.get(username=seller)
            buyer = User.objects.get(username=buyer)
            img = request.FILES["image"]
            uid = uuid.uuid4()
            fs = FileSystemStorage()
            fs.save(f"static/{uid}.jpg", img)
            url = fs.url(f"static/{uid}.jpg")
            ChatMessages.objects.create(
                seller=seller, buyer=buyer, content=str(url), is_picture=True)
            return JsonResponse({})
        except:
            return JsonResponse({"msg": "wrong message"})
    return JsonResponse({"msg": "failed"})


@csrf_exempt
def get_message(request: HttpRequest):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({"msg": "login first"})
    try:
        json_data = json.loads(request.body)
        last_idx = json_data["last_idx"]
    except:
        last_idx = None
    result = {"as_seller": {}, "as_buyer": {}}
    if last_idx:
        records = ChatMessages.objects.filter(
            idx__gt=int(last_idx), seller=user)
        as_seller = {record.idx: {"seller": f"{record.seller.username}",
                                  "buyer": f"{record.buyer.username}", "is_picture": str(record.is_picture),
                                  "content": str(record.content)} for record in records}
        records = ChatMessages.objects.filter(
            idx__gt=int(last_idx), buyer=user)
        as_buyer = {record.idx: {"seller": f"{record.seller.username}",
                                 "buyer": f"{record.buyer.username}", "is_picture": str(record.is_picture),
                                 "content": str(record.content)} for record in records}
        result["as_seller"] = as_seller
        result["as_buyer"] = as_buyer
    else:
        records = ChatMessages.objects.filter(seller=user)
        as_seller = {record.idx: {"seller": f"{record.seller.username}",
                                  "buyer": f"{record.buyer.username}", "is_picture": str(record.is_picture),
                                  "content": str(record.content)} for record in records}
        records = ChatMessages.objects.filter(buyer=user)
        as_buyer = {record.idx: {"seller": f"{record.seller.username}",
                                 "buyer": f"{record.buyer.username}", "is_picture": str(record.is_picture),
                                 "content": str(record.content)} for record in records}
        result["as_seller"] = as_seller
        result["as_buyer"] = as_buyer
    return JsonResponse(result)
