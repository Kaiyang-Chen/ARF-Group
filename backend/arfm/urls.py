"""arfm URL Configuration

The `urlpatterns` list routes URLs to views. For more information please see:
    https://docs.djangoproject.com/en/4.0/topics/http/urls/
Examples:
Function views
    1. Add an import:  from my_app import views
    2. Add a URL to urlpatterns:  path('', views.home, name='home')
Class-based views
    1. Add an import:  from other_app.views import Home
    2. Add a URL to urlpatterns:  path('', Home.as_view(), name='home')
Including another URLconf
    1. Import the include() function: from django.urls import include, path
    2. Add a URL to urlpatterns:  path('blog/', include('blog.urls'))
"""
from django.contrib import admin
from django.urls import path
from usersystem import views as user_views
from browser import views as browser_views


urlpatterns = [
    # admin
    path('admin/', admin.site.urls),
    # user system
    path('login/', user_views.login, name='login'),
    path('check/', user_views.check, name='check'),
    path('update/', user_views.update, name='update'),
    path('register/', user_views.register, name='register'),
    path('logout/', user_views.logout, name='logout'),
    path('delete/', user_views.delete, name='delete'),
    path('check_other/', user_views.check_other, name='check_other'),
    # browser
    path('fetch_home_products/', browser_views.fetch_home_products,
         name='fetch_home_products'),
    path('fetch_searched_products/', browser_views.fetch_searched_products,
         name='fetch_searched_products'),
    path('fetch_product_brief/', browser_views.fetch_product_brief,
         name='fetch_product_brief'),
    path('fetch_product_detailed/', browser_views.fetch_product_detailed,
         name='fetch_product_detailed')
]
