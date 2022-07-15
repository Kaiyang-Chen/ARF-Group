from django.db import models
from django.contrib.auth.models import User
# Create your models here.

class UserProfile(models.Model):
    user = models.OneToOneField(User, models.CASCADE, related_name='profile')
    gender = models.CharField(max_length=32, default='unknown')
    modified_time = models.DateTimeField(auto_now_add=True)
    phone = models.CharField(max_length=32, default='unknown')
    address = models.CharField(max_length=256, default='unknown')

    def __str__(self) -> str:
        return self.user.__str__()