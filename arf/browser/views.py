from django.http import HttpResponse, HttpRequest, JsonResponse
from django.contrib.auth import login as _login, logout as _logout, authenticate
import json
from login.models import UserProfile
from django.contrib.auth.models import User
import re
from django.views.decorators.csrf import csrf_exempt

# Create your views here.
