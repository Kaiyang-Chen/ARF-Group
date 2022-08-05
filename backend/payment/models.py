from django.db import models
from browser.models import ProductInfo
from django.contrib.auth.models import User
import uuid


class Trade(models.Model):
    seller = models.ForeignKey(
        User, models.CASCADE, related_name="seller_trade")
    buyer = models.ForeignKey(User, models.CASCADE, related_name="buyer_trade")
    done = models.BooleanField(default=False)
    product = models.ForeignKey(
        ProductInfo, models.CASCADE, related_name="product_trade")
    out_trade_no = models.UUIDField(
        primary_key=True, auto_created=True, default=uuid.uuid1)
