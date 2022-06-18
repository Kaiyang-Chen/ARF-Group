from django.db import models
from login.models import User

class ProductInfo(models.Model):
    name = models.CharField(max_length=128)
    owner = models.OneToOneField(User, models.CASCADE, related_name='owner')
    primary_class = models.CharField(max_length=128,default='unknown')
    secondary_class = models.CharField(max_length=128,default='unknown')
    color_style = models.CharField(max_length=32,default='unknown')
    price = models.IntegerField(default=-1)
    sold_state = models.BooleanField(default=False)
    popularity = models.Inte

    def __str__(self) -> str:
        return self.user.__str__()
# Create your models here.
