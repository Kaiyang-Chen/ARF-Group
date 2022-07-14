from django.db import models
from usersystem.models import User
import uuid

# Create your models here.



class ProductInfo(models.Model):
    UID = models.UUIDField(
        primary_key=True, auto_created=True, default=uuid.uuid1)
    name = models.CharField(max_length=128)
    description = models.CharField(max_length=1024, default="")
    owner = models.ForeignKey(User, models.CASCADE, related_name='owner')
    primary_class = models.CharField(max_length=128, default='unknown')
    secondary_class = models.CharField(max_length=128, default='unknown')
    color_style = models.CharField(max_length=32, default='unknown')
    price = models.FloatField(default=0)
    sold_state = models.BooleanField(default=False)