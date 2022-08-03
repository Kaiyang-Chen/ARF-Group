import json
import os
import uuid
import cv2
import subprocess

from browser.models import ProductInfo
from django.core.files.storage import FileSystemStorage
from django.http import HttpRequest, HttpResponse, JsonResponse
from django.views.decorators.csrf import csrf_exempt

# Create your views here.


def video_crop(input_video, output_dir):
    START_TIME = 0
    vidcap = cv2.VideoCapture(input_video)
    if vidcap.isOpened():
        rate = vidcap.get(cv2.CAP_PROP_FPS)
        frameNumber = vidcap.get(cv2.CAP_PROP_FRAME_COUNT)
        END_TIME = int(frameNumber/rate)
        fps = int(vidcap.get(cv2.CAP_PROP_FPS))

        frameToStart = START_TIME*fps

        frametoStop = END_TIME*fps

        vidcap.set(cv2.CAP_PROP_POS_FRAMES, frameToStart)

        success, image = vidcap.read()
        count = 0
        seconds = 1
        while success and frametoStop >= count:
            if count % (fps*seconds) == 0:
                save_path = output_dir + str(count) + ".jpg"
                cv2.imwrite(save_path, image)
                #print('Process %dth seconds: ' % int(count / (fps*seconds)), success)
            success, image = vidcap.read()
            count += 1


@csrf_exempt
def generate_ar(request: HttpRequest):
    # send: {"UID": uuid, "name": video_name}
    if not request.user.is_authenticated:
        return JsonResponse({"msg": "failed: login first"})
    if request.method == "POST":
        user = request.user.username
        try:
            data = json.loads(request.body)
            assert "UID" in data.keys()
            assert "name" in data.keys()
        except:
            return JsonResponse({"msg": "failed: invalid format"})
        id = uuid.UUID(data["UID"])
        video = data["name"]
        if not video.endswith(".mp4"):
            video = f"{video}.mp4"
        prod = ProductInfo.objects.filter(UID=id)
        if prod.exists():
            prod = prod[0]
            if prod.owner.username != user:
                return JsonResponse({"msg": "failed: permission denied"})
            if not os.path.isfile(f"static/{user}/{prod.name}/video/{video}"):
                return JsonResponse({"msg": "failed: video not exists"})
            else:
                if os.path.isdir(f"static/{user}/{prod.name}/ar_pics/"):
                    for filename in os.listdir(f"static/{user}/{prod.name}/ar_pics/"):
                        os.remove(
                            f"static/{user}/{prod.name}/ar_pics/{filename}")
                if os.path.isdir(f"static/{user}/{prod.name}/ar_model/"):
                    for filename in os.listdir(f"static/{user}/{prod.name}/ar_model/"):
                        os.remove(
                            f"static/{user}/{prod.name}/ar_model/{filename}")
                video_crop(f"static/{user}/{prod.name}/video/{video}",
                           f"static/{user}/{prod.name}/ar_pics/")
                cmd = f"export LD_LIBRARY_PATH=/root/AliceVision/install/lib && export PYTHONPATH=/root/meshroom && export PATH=/root/cmake-3.22.5-linux-x86_64/bin:/root/miniconda3/bin:/root/miniconda3/condabin:/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/games:/usr/local/games:/snap/bin:/root/AliceVision/install/bin:/root/meshroom && echo $PATH && systemd-run --scope python /root/meshroom/bin/meshroom_batch --input static/{user}/{prod.name}/ar_pics/  -o static/{user}/{prod.name}/ar_model/"
                f = open(
                    f"static/{user}/{prod.name}/ar_model/generation_log", 'w')
                subprocess.Popen(cmd, shell=True, stdin=f, stdout=f,
                                 stderr=f, close_fds=True, start_new_session=True)
                return JsonResponse({"msg": "AR model is generating..."})
        else:
            return JsonResponse({"msg": "failed: product not exists."})
    return JsonResponse({"msg": "failed"})


@csrf_exempt
def fetch_ar_model(request: HttpRequest):
    '''
    get the ar model and its texture
    send: {"UID": "uuid"}
    receive: {"name": "prodcut name", "texture": "a url of png", "ar_model": "a url of obj file"}
    '''
    if request.method == "POST":
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
            res = {"name": prod.name}
            if not os.path.isfile(f"static/{username}/{prod.name}/ar_model/texture_1001.png"):
                cmd = f"convert static/{username}/{prod.name}/ar_model/texture_1001.exr static/{username}/{prod.name}/ar_model/texture_1001.png"
                os.system(cmd)
                # subprocess.Popen(cmd, shell=True, stdin=None,
                #                  stdout=None, stderr=None, close_fds=True)
            if os.path.isfile(f"static/{username}/{prod.name}/ar_model/texture_1001.png") and os.path.isfile(f"static/{username}/{prod.name}/ar_model/texturedMesh.gltf"):
                fs = FileSystemStorage()
                url_texture = fs.url(
                    f"static/{username}/{prod.name}/ar_model/texture_1001.png")
                res["texture"] = str(url_texture)
                url_model = fs.url(
                    f"static/{username}/{prod.name}/ar_model/texturedMesh.gltf")
                res["ar_model"] = str(url_model)
                return JsonResponse(res)
            else:
                return HttpResponse("Failed: not AR model available")
    return JsonResponse({})
