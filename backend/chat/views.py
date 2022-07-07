from django.http import HttpRequest, HttpResponse


def chat(request: HttpRequest):
    return HttpResponse("test")
