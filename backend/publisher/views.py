import json
import os
import shutil
import uuid

from browser.models import ProductInfo
from django.core.files.storage import FileSystemStorage
from django.http import HttpRequest, HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt


@csrf_exempt
def post_product(request: HttpRequest):
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        try:
            json_data = json.loads(request.body)
        except:
            return HttpResponse('failed: invalid text')
        user = request.user
        if "name" not in json_data.keys():
            return HttpResponse('failed: no name')
        name = json_data["name"].strip()
        if len(name) >= 127:
            return HttpResponse('failed: name too long')
        if len(name) < 1:
            return HttpResponse('failed: username too short')
        for char in name:
            if (not char.isalnum()) and (not char in " @.+-_"):
                return HttpResponse('failed: name invalid')
        prods = ProductInfo.objects.filter(owner=user, name=name)
        if prods.exists():
            return HttpResponse('failed: repeated name')
        description = json_data["description"] if "description" in json_data.keys(
        ) else ""
        if len(description) > 1023:
            return HttpResponse('failed: description too long')
        primary_class = json_data["primary_class"] if "primary_class" in json_data.keys(
        ) else ""
        if len(primary_class) > 127:
            return HttpResponse('failed: primary_class too long')
        secondary_class = json_data["secondary_class"] if "secondary_class" in json_data.keys(
        ) else ""
        if len(secondary_class) > 127:
            return HttpResponse('failed: secondary_class too long')
        color_style = json_data["color_style"] if "color_style" in json_data.keys(
        ) else ""
        if len(color_style) > 127:
            return HttpResponse('failed: color_style too long')
        price = float(json_data["price"]
                      ) if "price" in json_data.keys() else 0.0
        prod = ProductInfo.objects.create(name=name, description=description,
                                          owner=user, primary_class=primary_class,
                                          secondary_class=secondary_class,
                                          color_style=color_style, price=price,
                                          sold_state=False)
        if not os.path.isdir(f"static/{user.username}"):
            os.mkdir(f"static/{user.username}")
        if not os.path.isdir(f"static/{user.username}/{name}"):
            os.mkdir(f"static/{user.username}/{name}")
        if not os.path.isdir(f"static/{user.username}/{name}/picture"):
            os.mkdir(f"static/{user.username}/{name}/picture")
        if not os.path.isdir(f"static/{user.username}/{name}/ar_model"):
            os.mkdir(f"static/{user.username}/{name}/ar_model")
        if not os.path.isdir(f"static/{user.username}/{name}/ar_pics"):
            os.mkdir(f"static/{user.username}/{name}/ar_pics")
        if not os.path.isdir(f"static/{user.username}/{name}/video"):
            os.mkdir(f"static/{user.username}/{name}/video")
        return JsonResponse({"UID": str(prod.UID)})
    return HttpResponse("failed")


@csrf_exempt
def post_picture(request: HttpRequest):
    # send: {"UID": uuid, "picture": picture_name}
    # one picture a time
    # you must be the owner of this product
    # if you want the picture to be the title, use "title" as its name
    # also overwrites the picture with the same name
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            pic = request.POST.get("picture")
            id = request.POST.get("UID")
            id = uuid.UUID(id)
        except:
            return JsonResponse({"msg": "1"})
        if not pic.endswith(".jpg"):
            pic = f"{pic}.jpg"
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")
            try:
                assert os.path.isdir(f"static/{user}/{prod.name}/picture")
            except:
                return HttpResponse("failed: internal error")
            if not request.FILES.get("image"):
                return HttpResponse("failed: no image found")
            if os.path.isfile(f"static/{user}/{prod.name}/picture/{pic}"):
                os.remove(f"static/{user}/{prod.name}/picture/{pic}")
            # will be removed first
            content = request.FILES['image']
            fs = FileSystemStorage()
            fs.save(f"static/{user}/{prod.name}/picture/{pic}", content)
            return JsonResponse({})
    return JsonResponse({"msg": "2"})


@csrf_exempt
def post_video(request: HttpRequest):
    # send: {"UID": uuid, "name": video_name}
    # one video a time
    # you must be the owner of this product
    # overwrites the picture with the same name
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            video = request.POST.get("name")
            id = request.POST.get("UID")
            id = uuid.UUID(id)
        except:
            return JsonResponse({"msg": "get info failed"})
        if not video.endswith(".MOV"):
            video = f"{video}.MOV"
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")
            try:
                assert os.path.isdir(f"static/{user}/{prod.name}/video")
            except:
                return HttpResponse("failed: internal error")
            if not request.FILES.get("video"):
                return HttpResponse("failed: no video found")
            if os.path.isfile(f"static/{user}/{prod.name}/video/{video}"):
                os.remove(f"static/{user}/{prod.name}/video/{video}")
            # will be removed first
            content = request.FILES['video']
            fs = FileSystemStorage()
            fs.save(f"static/{user}/{prod.name}/video/{video}", content)
            return JsonResponse({})
    return JsonResponse({"msg": "failed: wrong request method."})


@csrf_exempt
def delete_video(request: HttpRequest):
    # send: {"UID": uuid, "name": video_name}
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            data = json.loads(request.body)
            assert "UID" in data.keys()
            assert "name" in data.keys()
        except:
            return HttpResponse("failed: invalid format")
        id = uuid.UUID(data["UID"])
        video = data["name"]
        if not video.endswith(".MOV"):
            video = f"{video}.MOV"
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")
            if os.path.isfile(f"static/{user}/{prod.name}/video/{video}"):
                os.remove(f"static/{user}/{prod.name}/video/{video}")
            return HttpResponse("successful")
    return HttpResponse("failed")


@csrf_exempt
def delete_picture(request: HttpRequest):
    # send: {"UID": uuid, "picture": picture_name}
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            data = json.loads(request.body)
            assert "UID" in data.keys()
            assert "picture" in data.keys()
        except:
            return HttpResponse("failed: invalid format")
        id = uuid.UUID(data["UID"])
        pic = data["picture"]
        if not pic.endswith(".jpg"):
            pic = f"{pic}.jpg"
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")
            if os.path.isfile(f"static/{user}/{prod.name}/picture/{pic}"):
                os.remove(f"static/{user}/{prod.name}/picture/{pic}")
            return HttpResponse("successful")
    return HttpResponse("failed")


@csrf_exempt
def update_product(request: HttpRequest):
    # send: {"UID": "frgtryut234t5", "name": "new name"}
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            json_data = json.loads(request.body)
            assert "UID" in json_data.keys()
        except:
            return HttpResponse("failed: invalid format")
        id = uuid.UUID(json_data["UID"])
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")

            name = None
            if "name" in json_data.keys():
                name = json_data["name"].strip()
                if len(name) >= 127:
                    return HttpResponse('failed: name too long')
                if len(name) < 1:
                    return HttpResponse('failed: username too short')
                for char in name:
                    if (not char.isalnum()) and (not char in " @.+-_"):
                        return HttpResponse('failed: name invalid')
                prods = ProductInfo.objects.filter(
                    owner=request.user, name=name)
                if prods.exists():
                    return HttpResponse('failed: name taken by another product')

            description = json_data["description"] if "description" in json_data.keys(
            ) else None
            if description is not None and len(description) > 1023:
                return HttpResponse('failed: description too long')

            primary_class = json_data["primary_class"] if "primary_class" in json_data.keys(
            ) else None
            if primary_class is not None and len(primary_class) > 127:
                return HttpResponse('failed: primary_class too long')

            secondary_class = json_data["secondary_class"] if "secondary_class" in json_data.keys(
            ) else None
            if secondary_class is not None and len(secondary_class) > 127:
                return HttpResponse('failed: secondary_class too long')

            color_style = json_data["color_style"] if "color_style" in json_data.keys(
            ) else None
            if color_style is not None and len(color_style) > 127:
                return HttpResponse('failed: color_style too long')

            price = float(json_data["price"]
                          ) if "price" in json_data.keys() else None

            if name is not None:
                os.rename(f"static/{user}/{prod.name}",
                          f"static/{user}/{name}")
                prod.name = name
            if description is not None:
                setattr(prod, "description", description)
            if primary_class is not None:
                setattr(prod, "primary_class", primary_class)
            if secondary_class is not None:
                setattr(prod, "secondary_class", secondary_class)
            if color_style is not None:
                setattr(prod, "color_style", color_style)
            if price is not None:
                setattr(prod, "price", price)
            prod.save()
            return HttpResponse("successful")
    return HttpResponse("failed")


@csrf_exempt
def delete_product(request: HttpRequest):
    # send: {"UID": uuid}
    if not request.user.is_authenticated:
        return HttpResponse("failed: login first")
    if request.method == "POST":
        user = request.user.username
        try:
            data = json.loads(request.body)
            assert "UID" in data.keys()
        except:
            return HttpResponse("failed: invalid format")
        id = uuid.UUID(data["UID"])
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return HttpResponse("failed: permission denied")
            if os.path.isdir(f"static/{user}/{prod.name}"):
                shutil.rmtree(f"static/{user}/{prod.name}")
            prod.delete()
            return HttpResponse("successful")
    return HttpResponse("failed")
