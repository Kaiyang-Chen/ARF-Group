from django.db import models
from login.models import User


class ProductInfo(models.Model):
    name = models.CharField(max_length=128)
    owner = models.OneToOneField(User, models.CASCADE, related_name='owner')
    primary_class = models.CharField(max_length=128, default='unknown')
    secondary_class = models.CharField(max_length=128, default='unknown')
    color_style = models.CharField(max_length=32, default='unknown')
    price = models.IntegerField(default=-1)
    sold_state = models.BooleanField(default=False)


class SearchingRecords(models.Model):
    product = models.OneToOneField(
        ProductInfo, models.CASCADE, related_name='popularity')
    user = models.OneToOneField(
        User, models.CASCADE, related_name='popularity')
    access_date = models.DateField(auto_now_add=True)


class ShoppingCartRecords(models.Model):
    product = models.OneToOneField(
        ProductInfo, models.CASCADE, related_name='popularity')
    user = models.OneToOneField(
        User, models.CASCADE, related_name='popularity')
    access_date = models.DateField(auto_now_add=True)
