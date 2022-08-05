import django.utils.timezone as timezone
from browser.models import ProductInfo
from django.contrib.auth.models import User
from django.db import models


class ShoppingCart(models.Model):
    owner = models.ForeignKey(User, models.CASCADE, related_name='owner1')
    prod = models.ForeignKey(ProductInfo, models.CASCADE, related_name='prod1')
    add_date = models.DateTimeField(default=timezone.now)
