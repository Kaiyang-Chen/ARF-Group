from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import login as _login, logout as _logout, authenticate
import json
from login.models import UserProfile
from django.contrib.auth.models import User
import re
from django.views.decorators.csrf import csrf_exempt
# Create your views here.


def check_other(request: HttpRequest):
    if request.method == 'GET':
        user = request.user
        try:
            result = json.loads(request.body)
        except:
            return HttpResponse('failed: invalid text')
        if not user.is_authenticated:
            return HttpResponse('failed: login first')
        if "username" not in result.keys():
            return HttpResponse('failed: try api check')
        username = result['username']
        user = None
        try:
            user = User.objects.get(username=username)
        except:
            return HttpResponse('failed: no such username')
        userprofile = UserProfile.objects.get(user=user)
        for key in result.keys():
            if key != 'password':
                if hasattr(user, key):
                    result[key] = getattr(user, key)
                elif hasattr(userprofile, key):
                    result[key] = getattr(userprofile, key)
        return JsonResponse(result)
    return HttpResponse('failed')


def check(request: HttpRequest):
    '''
    Require information
    '''
    if request.method == 'GET':
        user = request.user
        try:
            result = json.loads(request.body)
        except:
            return HttpResponse('failed: invalid text')
        if not user.is_authenticated:
            return HttpResponse('failed: login first')
        userprofile = UserProfile.objects.get(user=user)
        for key in result.keys():
            if key != 'password':
                if hasattr(user, key):
                    result[key] = getattr(user, key)
                elif hasattr(userprofile, key):
                    result[key] = getattr(userprofile, key)
        return JsonResponse(result)
    return HttpResponse('failed')


@csrf_exempt
def update(request: HttpRequest):
    '''
    Update information
    '''
    if request.method == 'POST':
        user = request.user
        if not user.is_authenticated:
            return HttpResponse('failed: login first')
        try:
            result = json.loads(request.body)
        except:
            return HttpResponse('failed: invalid text')
        userprofile = UserProfile.objects.get(user=user)
        # password
        password = None
        if 'password' in result.keys():
            password = result['password'].strip()
            if (len(password) < 8 or len(password) > 20):
                return HttpResponse('failed: illegal password length: 8-20 required')
            if (not re.search("[a-z]", password)) or (not re.search("[A-Z]", password)):
                return HttpResponse('failed: a-z and A-Z characters required')
            if not re.search("[0-9]", password):
                return HttpResponse('failed: numbers required')
        # username
        username = None
        if 'username' in result.keys():
            username = result['username']
            username = username.strip()
            if len(username) >= 100:
                return HttpResponse('failed: username too long')
            if len(username) < 1:
                return HttpResponse('failed: username too short')
            for char in username:
                if (not char.isalnum()) and (not char in "@/./+/-/_"):
                    return HttpResponse('failed: username invalid')
            userinfo = User.objects.filter(username=username)
            if userinfo.exists():
                return HttpResponse('failed: username exists')
        # email
        email = None
        if 'email' in result.keys() and result['email'] != '':
            email = result['email'].strip()
            if len(email) > 100:
                return HttpResponse('failed: email too long')
            if '@' not in email:
                return HttpResponse('failed: email invalid')
            userinfo = User.objects.filter(email=email)
            if userinfo.exists():
                return HttpResponse('failed: email exists')
        if 'password' in result.keys():
            user.set_password(password)
        if 'username' in result.keys():
            user.username = username
        if 'email' in result.keys():
            user.email = email
        if "first_name" in result.keys() and len(result['first_name']) < 32:
            user.first_name = result['first_name']
        if "last_name" in result.keys() and len(result['last_name']) < 32:
            user.last_name = result['last_name']
        if "gender" in result.keys() and result['gender'] in ['male', 'female']:
            userprofile.gender = result['gender']
        if "phone" in result.keys() and len(result['phone']) < 32 and result['phone'].isnumeric():
            userprofile.phone = result['phone']
        if "address" in result.keys() and len(result['address']) < 256:
            userprofile.address = result['address']
        user.save()
        userprofile.save()
        return HttpResponse('successful')
    return HttpResponse('failed')


@csrf_exempt
def login(request: HttpRequest):
    '''
    Log in
    '''
    if request.method == 'POST':
        json_data = None
        try:
            json_data = json.loads(request.body)
            username = json_data['username']
            password = json_data['password']
        except:
            return HttpResponse("failed")
        user = authenticate(username=username, password=password)
        if user:
            _login(request, user)
            return HttpResponse("successful")
    return HttpResponse("failed")


@csrf_exempt
def register(request: HttpRequest):
    '''
    Create a new record and do the least amount of check
    '''
    if request.method == 'POST':
        try:
            json_data = json.loads(request.body)
        except:
            return HttpResponse('failed: invalid text')

        # username check
        username = json_data['username']
        username = username.strip()
        if len(username) >= 100:
            return HttpResponse('failed: username too long')
        if len(username) < 1:
            return HttpResponse('failed: username too short')
        for char in username:
            if (not char.isalnum()) and (not char in "@/./+/-/_"):
                return HttpResponse('failed: username invalid')
        userinfo = User.objects.filter(username=username)
        if userinfo.exists():
            return HttpResponse('failed: username exists')
        # email check
        email = None
        if 'email' in json_data.keys() and json_data['email'] != '':
            email = json_data['email'].strip()
            if len(email) > 100:
                return HttpResponse('failed: email too long')
            if '@' not in email:
                return HttpResponse('failed: email invalid')
            userinfo = User.objects.filter(email=email)
            if userinfo.exists():
                return HttpResponse('failed: email exists')
        # password check
        password = json_data['password'].strip()
        if (len(password) < 8 or len(password) > 20):
            return HttpResponse('failed: illegal password length: 8-20 required')
        if (not re.search("[a-z]", password)) or (not re.search("[A-Z]", password)):
            return HttpResponse('failed: a-z and A-Z characters required')
        if not re.search("[0-9]", password):
            return HttpResponse('failed: numbers required')
        user = User.objects.create_user(
            username=username, email=email, password=password)
        userprofile = UserProfile.objects.create(user=user)
        # creation finished
        # first name and second name
        if "first_name" in json_data.keys() and len(json_data['first_name']) < 32:
            user.first_name = json_data['first_name']
        if "last_name" in json_data.keys() and len(json_data['last_name']) < 32:
            user.last_name = json_data['last_name']
        # gender, modified_time, phone, address
        if "gender" in json_data.keys() and json_data['gender'] in ['male', 'female']:
            userprofile.gender = json_data['gender']
        if "phone" in json_data.keys() and len(json_data['phone']) < 32 and json_data['phone'].isnumeric():
            userprofile.phone = json_data['phone']
        if "address" in json_data.keys() and len(json_data['address']) < 256:
            userprofile.address = json_data['address']
        user.save()
        userprofile.save()
        return HttpResponse('successful')
    return HttpResponse('failed')


def logout(request: HttpRequest):
    '''
    Log out
    '''
    if request.user.is_authenticated:
        _logout(request)
        return HttpResponse("successful")
    return HttpResponse("failed")


def delete(request: HttpRequest):
    '''
    Delete the current user
    '''
    user = request.user
    if not user.is_authenticated:
        return HttpResponse('failed: login first')
    user.delete()
    _logout(request)
    return HttpResponse('successful')
