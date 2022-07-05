import json
import os
import random
import uuid

from django.contrib.auth.models import User
from django.core.files.storage import FileSystemStorage
from django.http import HttpRequest, JsonResponse

from browser.models import ProductInfo

# from django.views.decorators.csrf import csrf_exempt


def fetch_home_products(request: HttpRequest):
    # get at most 64 uids
    # example:
    # {"0": "13233412-34xewrcr", "1": "7645ytewerg-vewegf"}
    def get_random(how_many=64):
        count = ProductInfo.objects.filter(sold_state=False).count()
        result = {}
        idx = 0
        if count <= how_many:
            for prod in ProductInfo.objects.filter(sold_state=False):
                result[f"{idx}"] = f'{getattr(prod,"UID")}'
                idx += 1
        else:
            samples = random.sample(range(count), 64)
            prods = [ProductInfo.filter(sold_state=False)[i] for i in samples]
            for prod in prods:
                result[f"{idx}"] = f'{getattr(prod,"UID")}'
                idx += 1
        return result
    if request.method == 'GET':
        user = request.user
        if not user.is_authenticated:
            return JsonResponse(get_random())
        else:
            if not os.path.isdir(f"static/{getattr(user,'username')}"):
                os.mkdir(f"static/{getattr(user,'username')}")
            if not os.path.isfile(f"static/{getattr(user,'username')}/history"):
                file = open(f"static/{getattr(user,'username')}/history", "w")
                file.write(json.dumps([]))
                file.close()
                return JsonResponse(get_random())
            file = open(f"static/{getattr(user,'username')}/history", "r")
            hist = json.loads(file.read())
            res = []
            prim = {}
            secd = {}
            color = {}
            for rec in hist:
                prim[rec["primary_class"]] = 1 if rec["primary_class"] not in prim.keys(
                ) else prim[rec["primary_class"]]+1
                secd[rec["secondary_class"]] = 1 if rec["secondary_class"] not in secd.keys(
                ) else secd[rec["secondary_class"]]+1
                color[rec["color_style"]] = 1 if rec["color_style"] not in color.keys(
                ) else color[rec["color_style"]]+1
            prim = sorted([(key, prim[key]) for key in prim.keys()],
                          key=lambda y: y[1], reverse=True)
            secd = sorted([(key, secd[key]) for key in secd.keys()],
                          key=lambda y: y[1], reverse=True)
            color = sorted([(key, color[key])
                            for key in color.keys()], key=lambda y: y[1], reverse=True)
            file.close()
            for pr in prim:
                for se in secd:
                    for co in color:
                        prods = ProductInfo.objects.filter(
                            primary_class=pr[0], secondary_class=se[0], color_style=co[0], sold_state=False)
                        if prods.count() >= 32:
                            samples = random.sample(range(prods.count()), 32)
                            prods = [prods[i] for i in samples]
                        for prod in prods:
                            if str(prod.UID) not in res:
                                res.append(str(prod.UID))
                        if len(res) >= 64:
                            res = {f"{idx}": res[idx] for idx in range(64)}
                            return JsonResponse(res)
            for pr in prim:
                for se in secd:
                    prods = ProductInfo.objects.filter(
                        primary_class=pr[0], secondary_class=se[0], sold_state=False)
                    if prods.count() >= 32:
                        samples = random.sample(range(prods.count()), 32)
                        prods = [prods[i] for i in samples]
                    for prod in prods:
                        if str(prod.UID) not in res:
                            res.append(str(prod.UID))
                    if len(res) >= 64:
                        res = {f"{idx}": res[idx] for idx in range(64)}
                        return JsonResponse(res)
            for pr in prim:
                prods = ProductInfo.objects.filter(
                    primary_class=pr[0], sold_state=False)
                if prods.count() >= 32:
                    samples = random.sample(range(prods.count()), 32)
                    prods = [prods[i] for i in samples]
                for prod in prods:
                    if str(prod.UID) not in res:
                        res.append(str(prod.UID))
                if len(res) >= 64:
                    res = {f"{idx}": res[idx] for idx in range(64)}
                    return JsonResponse(res)
            tentatives = get_random(64-len(res))
            for id in tentatives.values():
                if id not in res:
                    res.append(id)
            res = {f"{idx}": res[idx] for idx in range(min(64, len(res)))}
            return JsonResponse(res)
    return JsonResponse({})


def fetch_searched_products(request: HttpRequest):
    # get at most 64 uids
    # example: {"keywords": "sofa big", "owner": "xingyanwan", "primary_class": "living room", "secondary_class": "sofa", "color_style": "blue", "price_gt": "100", "price_lt": "500", starts_from: "128"}
    if request.method == 'GET':
        try:
            conditions = json.loads(request.body)
            assert type(conditions) == dict and conditions != {}
        except:
            return JsonResponse({})
        startidx = 0 if "starts_from" not in conditions.keys() else int(
            conditions["starts_from"])
        prods = ProductInfo.objects.filter(sold_state=False)
        if "keywords" in conditions.keys():
            for word in conditions["keywords"].split():
                a = prods.filter(name__icontains=word)
                b = prods.filter(description__icontains=word)
                prods = a | b
        if "owner" in conditions.keys():
            owner = User.objects.filter(username=conditions["owner"])
            if owner.exists():
                user = owner.first()
                prods = prods.filter(owner=user)
            else:
                return JsonResponse({})
        if "primary_class" in conditions.keys():
            prods = prods.filter(
                primary_class__icontains=conditions["primary_class"])
        if "secondary_class" in conditions.keys():
            prods = prods.filter(
                secondary_class__icontains=conditions["secondary_class"])
        if "color_style" in conditions.keys():
            prods = prods.filter(
                color_style__icontains=conditions["color_style"])
        if "price_gt" in conditions.keys():
            prods = prods.filter(price__gte=int(conditions["price_gt"]))
        if "price_lt" in conditions.keys():
            prods = prods.filter(price__lte=int(conditions["price_lt"]))
        if prods.count() <= startidx:
            return JsonResponse({})
        endidx = min(startidx+64, prods.count())
        prods = prods[startidx:endidx]
        res = {f"{idx}": f"{prods[idx].UID}" for idx in range(endidx-startidx)}
        return JsonResponse(res)
    return JsonResponse({})


def fetch_product_brief(request: HttpRequest):
    # get the brief info
    # {"UID": "afrtr-43gtwwf"}
    # {"name": "good sofa", "description": "this is a sofa", "price": 200, "picture": "some url"}
    if request.method == "GET":
        try:
            req = json.loads(request.body)
            id = req["UID"]
            id = uuid.UUID(id)
        except:
            return JsonResponse({})
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            username = prod.owner.username
            res = {"name": prod.name, "description": prod.description,
                   "price": f"{prod.price}"}
            if os.path.isfile(f"static/{username}/{prod.name}/picture/title.jpg"):
                fs = FileSystemStorage()
                url = fs.url(f"{username}/{prod.name}/picture/title.jpg")
                res["picture"] = str(url)
            return JsonResponse(res)
    return JsonResponse({})

def fetch_product_detailed(request:HttpRequest):
    # literately get every bit of information except ar model
    # send: {"UID": "er43t5y6juyki"}
    # receive: {"UID":prod.id,"name": prod.name, "description": prod.description,
    # "owner":username, "primary_class": prod.primary_class,
    # "secondary_class": prod.secondary_class, "color_style": prod.color_style,
    # "price": prod.price, "sold_state": prod.sold_state,
    # "picture_0": url0, "picture_1": url1}
    if request.method == "GET":
        try:
            req = json.loads(request.body)
            id = req["UID"]
            id = uuid.UUID(id)
        except:
            return JsonResponse({})
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            username = prod.owner.username
            res = {"UID":str(id),"name": prod.name, "description": prod.description,
                   "owner":username,"primary_class":prod.primary_class,"secondary_class":prod.secondary_class,"color_style":prod.color_style,"price": f"{prod.price}","sold_state":str(prod.sold_state)}
            if os.path.isdir(f"static/{username}/{prod.name}/picture"):
                fs = FileSystemStorage()
                pics = os.listdir(f"static/{username}/{prod.name}/picture")
                for pic in pics: 
                    url= fs.url(f"{username}/{prod.name}/picture/{pic}")
                    res[pic.strip(".jpg")] = str(url)
            if request.user.is_authenticated:
                hist = {"UID":res["UID"],"primary_class":res["primary_class"],
                        "secondary_class":res["secondary_class"],
                        "color_style":res["color_style"]}
                if not os.path.isdir(f"static/{request.user.username}"):
                    os.mkdir(f"static/{request.user.username}")
                if not os.path.isfile(f"static/{request.user.username}/history"):
                    file = open(f"static/{request.user.username}/history","w")
                    file.write(json.dumps([hist]))
                    file.close()
                else:
                    file = open(f"static/{request.user.username}/history","r")
                    hists = json.loads(file.read())
                    file.close()
                    if len(hists)>63:
                        hists = hists[-63:]
                        # only keep 64 records
                    hists.append(hist)
                    with open(f"static/{request.user.username}/history","w") as file:
                        file.write(json.dumps(hists))
            return JsonResponse(res)
    return JsonResponse({})
        