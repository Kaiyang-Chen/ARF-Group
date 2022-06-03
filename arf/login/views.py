from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import login as _login, logout as _logout, authenticate
import json
from login.models import UserProfile
from django.contrib.auth.models import User
import re
# Create your views here.


def check(request: HttpRequest):
    '''
    Require information
    '''
    if request.method == 'GET':
        user = request.user
        result = json.loads(request.body)
        if not user.is_authenticated():
            return HttpResponse('failed: login first')
        userprofile = UserProfile.objects.get(user=user)
        for key in result.keys():
            if key != 'password':
                if key in user.__dict__.keys():
                    result[key] = user.__dict__[key]
                elif key in userprofile.__dict__.keys():
                    result[key] = userprofile.__dict[key]
        return JsonResponse(result)
    return HttpResponse('failed')


def update(request: HttpRequest):
    '''
    Update information
    '''
    if request.method == 'POST':
        user = request.user
        result = json.loads(request.body)
        if not user.is_authenticated():
            return HttpResponse('failed: login first')
        userprofile = UserProfile.objects.get(user=user)
        for key in result.keys():
            if key == 'password':
                password = result[key].strip()
                ok = True
                if (len(password) < 8 or len(password) > 20):
                    ok = False
                elif (not re.search("[a-z]", password)) or (not re.search("[A-Z]", password)):
                    ok = False
                elif not re.search("[0-9]", password):
                    ok = False
                if ok:
                    user.set_password(password)
            elif key == 'username':
                test_user = User.objects.filter(username=result[key].strip())
                if not test_user.exists():
                    user.username = result[key].strip()
            elif key in user.__dict__.keys():
                setattr(user, key, result[key])
            elif key in userprofile.__dict__.keys():
                setattr(userprofile, key, result[key])
        user.save()
        userprofile.save()
        return HttpResponse('successful')
    return HttpResponse('failed')


def login(request: HttpRequest):
    '''
    Log in
    '''
    if request.method == 'POST':
        json_data = json.loads(request.body)
        username = json_data['username']
        password = json_data['password']
        user = authenticate(username=username, password=password)
        if user:
            _login(request, user)
        return HttpResponse("successful")
    return HttpResponse("failed")


def register(request: HttpRequest):
    '''
    Create a new record and do the least amount of check
    '''
    if request.method == 'POST':
        json_data = json.loads(request.body)
        username = json_data['username']
        username = username.strip()
        userinfo = User.objects.filter(username=username)
        if userinfo.exists():
            return HttpResponse('failed: username exists')
        email = None
        if 'email' in json_data.keys() and json_data['email'] != '':
            email = json_data['email']
            userinfo = User.objects.filter(email=email)
            if userinfo.exists():
                return HttpResponse('failed: email exists')
            email = email
        password = json_data['password'].strip()
        if (len(password) < 8 or len(password) > 20):
            return HttpResponse('failed: illegal password length')
        if (not re.search("[a-z]", password)) or (not re.search("[A-Z]", password)):
            return HttpResponse('failed: characters required')
        if not re.search("[0-9]", password):
            return HttpResponse('failed: numbers required')
        user = User.objects.create_user(
            username=username, email=email, password=password)
        userprofile = UserProfile.objects.create(user=user)
        for key in json_data.keys():
            if key not in ['username', 'email', 'password']:
                if key in user.__dict__.keys():
                    setattr(user, key, json_data[key])
                elif key in UserProfile.__dict__.keys():
                    setattr(userprofile, key, json_data[key])
        user.save()
        userprofile.save()
        return HttpResponse('successful')
    return HttpResponse('failed')


def logout(request: HttpRequest):
    '''
    Log out
    '''
    _logout(request)
    return HttpResponse("successful")


def delete(request: HttpRequest):
    '''
    Delete the current user
    '''
    if request.method == 'DELETE':
        user = request.user
        if not user.is_authenticated():
            return HttpResponse('failed: login first')
        user.delete()
        _logout(request)
        return HttpResponse('successful')
    return HttpResponse('failed')
