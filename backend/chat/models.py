from django.db import models
from django.contrib.auth.models import User


class ChatMessages(models.Model):
    seller = models.ForeignKey(User, models.CASCADE, "seller")
    buyer = models.ForeignKey(User, models.CASCADE, "buyer")
    create_time = models.DateTimeField(auto_now_add=True)
    idx = models.AutoField(primary_key=True)
    content = models.CharField(max_length=256, default="")
    is_picture = models.BooleanField(default=False)
    