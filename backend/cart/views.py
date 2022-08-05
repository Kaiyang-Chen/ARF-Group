import json
import uuid

import django.utils.timezone as timezone
from browser.models import ProductInfo
from django.http import HttpRequest, JsonResponse
from django.views.decorators.csrf import csrf_exempt

from cart.models import ShoppingCart


@csrf_exempt
def add_to_cart(request: HttpRequest):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({"msg": "failed: log in first"})
    try:
        json_data = json.loads(request.body)
        uid = uuid.UUID(json_data["UID"])
    except:
        return JsonResponse({"msg": "failed: wrong message"})
    try:
        prod = ProductInfo.objects.get(UID=uid)
    except:
        return JsonResponse({"msg": "cannot find such product"})
    prods = ShoppingCart.objects.filter(owner=user, prod=prod)
    if prods.exists():
        prods[0].add_data = timezone.now
        prods[0].save()
        return JsonResponse({"msg": "already added"})
    prod = ShoppingCart.objects.create(owner=user, prod=prod)
    return JsonResponse({"msg": "successful"})


@csrf_exempt
def delete_from_cart(request: HttpRequest):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({"msg": "failed: log in first"})
    try:
        json_data = json.loads(request.body)
        uid = uuid.UUID(json_data["UID"])
    except:
        return JsonResponse({"msg": "failed: wrong message"})
    try:
        prod = ProductInfo.objects.get(UID=uid)
    except:
        return JsonResponse({"msg": "cannot find such product"})
    prods = ShoppingCart.objects.filter(owner=user, prod=prod)
    if prods.exists():
        prods[0].delete()
        return JsonResponse({"msg": "successful"})
    return JsonResponse({"msg": "no such record on the server"})


def get_cart(request: HttpRequest):
    user = request.user
    if not user.is_authenticated:
        return JsonResponse({"msg": "failed: log in first"})
    results = ShoppingCart.objects.filter(owner=user).order_by("-add_date")
    response = {f"{i}": str(record.prod.UID)
                for i, record in enumerate(results)}
    return JsonResponse(response)
