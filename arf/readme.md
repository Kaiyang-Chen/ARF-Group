# AR-enabled-furniture-trading-plateform
Capstone project (VE441 App Dev for Entrepreneurs) for SJTU-UM Joint Institute

## Build

Backend
- Django 4.0.4
- PostgreSQL 14.3

Frontend
- Android API level 31
- Andriod Studio
- ARCore SDK for Android 1.31.0

## Model and Engine

### Storymap

#### Buyers
![graph](/readme_graphs/buyers.png "Buyers")

#### Sellers
![graph](/readme_graphs/sellers.png "Sellers")

### Data and Control Blocks
![graph](/readme_graphs/block.png "Sellers")

The primary functional blocks of the app include Dashboard, Product Publisher (and Model Generator), Product Browser and Chatting Manager. Payment module will be likely to be done with AliPay or Wechat Pay api.

Dashboard is used to manage user information, which supports pre-defined information fields such as user name, email address, phone number, age, gender, address, etc. Dashboard supports logging in/out, registering and deleting users. In addition, this module is responsible for use authentication, whose service is provided by Django. When the user wants to update user information, the front-end sends a GET request to the Dashboard module, and the module requests Authentication and User Profile Database for the required information and sends back to the front-end. When updating fields, the front-end sends a POST request to the Dashboard and the Dashboard updates the database.

Product Browser is used to search for existing products. The front-end sends a GET request to Product Browser with reqired fields. If the request wants a specific product with its UID, all information about this product is fetched and sent back to the front-end. If the request wants some products that satisfiy the searching criterial, Product Browser sends 5-10 results that are wanted to the front-end. Front-end may indicate specific fields that should be returned in this case. All the product information not including the AR model is stored in one seperate Product Database that is not the same as User Profile Database. This database also includes the file location of the corresponding AR model. To require an AR model for a specific product, its UID will be provided by the front-end and Browser searches for the targeted file. All user will have browsing history stored, so that the front-end can request for user recommendation on the welcoming homepage without providing any additional information. Product Browser is connected and managing two parts of storage: basic product information database and AR model files.

Porduct Publisher is for buyers to publish new products. The front-end sends a POST request to the module including basic product information and pictures of the product from multiple angles. Product Publisher organizes, verifies and fills in the basic information and pushes updates to Product Database, and gives it a unique UID. In the meantime, Product Publisher passes on the materials needed to generate the AR model to the Model Generator. Model Generator processes the materials in an async way. When model generation (which will take some time) is done, Model Generator will inform the front-end so that the user may request for the AR model through Product Browser and do necessary adjustment (such as adding more angles for a better model). Product Publisher is also responsible for updating product information and AR models.

Chatting Manager supports establishing chats between buyers and sellers. After the buyer searches for a product and gets the owner's username, the front-end sends the username to Chatting Manager so that this module creates a chatting session between the buyer and the seller. Each side of the chatting session may choose to create/post chat/get chat/delete session.

The display of the AR model is done through ARCore SDK for Android which is handled by the front-end device.

## APIs and Controller

### User management

SERVER_IP/check/

Get the user's information. A GET request with a body containing the required fields. E.g., {"username":"", "gender":""}
Returns a HttpResponse indicating sucess or failure.

SERVER_IP/check_other/

SERVER_IP/update/

Update the user's information. A POST request with a body containing the required fields. E.g., {"username":"new_name", "gender":"male"}
Returns a HttpResponse indicating sucess or failure.

SERVER_IP/login/

Try to log in a session with user name and password. E.g., {"username":"cky", "password":"1234567"}
Returns a HttpResponse indicating sucess or failure.

SERVER_IP/register/

Try register a new user with user name and password. Additional fields may be provided. Otherwise, these fields will be default values. E.g., {"username":"cky", "password":"1234567", "address":"xxx"}
Returns a HttpResponse indicating sucess or failure.

SERVER_IP/logout/

Log out the session.

SERVER_IP/delete/

Delete the current user. The user must log in first.

! all returned values by the server are strings
! all received by the server is assumed to be strings
! do conversions first
! you do not need arguments if you do not want to specify

# browser
path('fetch_home_products/', browser_views.fetch_home_products,
     name='fetch_home_products')

get at most 64 UID for homepage
GET method
send: anything
receive: {"0": "13233412-34xewrcr", "1": "7645ytewerg-vewegf"}

path('fetch_searched_products/', browser_views.fetch_searched_products,
     name='fetch_searched_products')

get at most 64 uids
GET method
send: {"keywords": "sofa big", "owner": "xingyanwan", "primary_class": "living room", "secondary_class": "sofa", "color_style": "blue", "price_gt": "100", "price_lt": "500", "starts_from": "128"}
! starts_from: for example, if you have checked 64 results and want to see more results, set starts_from = 64

receive: {"0": "13233412-34xewrcr", "1": "7645ytewerg-vewegf"}

path('fetch_product_brief/', browser_views.fetch_product_brief,
     name='fetch_product_brief')

get the brief info
GET method
send: {"UID": "afrtr-43gtwwf"}
receive: {"name": "good sofa", "description": "this is a sofa", "price": 200, "picture": "some url"}
! picture is the url of the title page, the name of this picture is title.jpg in FS

path('fetch_product_detailed/', browser_views.fetch_product_detailed,
     name='fetch_product_detailed')

get all the information except AR model
GET method
send: {"UID": "afrtr-43gtwwf"}
receive: {"UID": prod.id, "name": prod.name, "description": prod.description,
"owner": username, "primary_class": prod.primary_class,
"secondary_class": prod.secondary_class, "color_style": prod.color_style,
"price": prod.price, "sold_state": prod.sold_state,
"picture_0": url0, "picture_1": url1}
! all pictures will be sent, test.jpg on FS will result in "test": some_url
! primary_class is level-1 classification like kitchen, living room, bathroom; for recommendation and searching
! secondary_class is level-2 classfication like sofa, chair; for recommendation and searching

# publisher
path('post_product/', publisher_views.post_product, name='post_product')

post a product, without picture
must login first with cookie in the request
POST method
send: {"name": prod.name, "description": prod.description, "primary_class": prod.primary_class,
"secondary_class": prod.secondary_class, "color_style": prod.color_style, "price": prod.price}
receive: HttpResponse starts with failed or {"UID": "tw5y65we3t4sdv"}

path('post_picture/', publisher_views.post_picture, name='post_picture')

post a picture
must login first with cookie in the request, you must be the owner
POST method
send: {"UID": "wdefargstrdtyu", "picture": "pic_name"}
! like lab2 request.FILES["image"] has the file
receive: HttpResponse starts with successful or failed
! on FS, the new picture will be named as "pic_name.jpg"
! name it "title" if you want it as the result picture in fetch_product_brief
! if "pic_name.jpg" exists, it will be overwritten

path('delete_picture/', publisher_views.delete_picture, name='delete_picture')

delete a picture
must login first with cookie in the request, you must be the owner
POST method
send: {"UID": "wdefargstrdtyu", "picture": "pic_name"}
receive: HttpResponse starts with successful or failed

path('update_product/', publisher_views.update_product, name='update_product')

refer to post_product
except UID must be provided

path('delete_product/', publisher_views.delete_product, name='delete_product')

delete a product
must login first with cookie in the request, you must be the owner
POST method
send: {"UID": "wdefargstrdtyu"}
receive: HttpResponse starts with successful or failed

### Purchase

SERVER_IP/buy_product/

Try to buy the product. Send a GET request with a body including its UID. E.g., {"UID":"12323"} Returns an HttpResponse indicating success or failure.

### Chat

SERVER_IP/start_session/

Create a chatting session. Send a POST request with a body including target username. E.g., {"username":"cky"} Returns an HttpResponse indicating success or failure.

SERVER_IP/post_chatt/

Post a chatting message. Send a POST request with a body such as {"message":"hello"}. Returns an empty HttpResponse.

SERVER_IP/get_chatt/

Get chatting messages. Send an empty GET request. Returns an HttpResponse such as {"message":{"cky":"hello","cky2":"hi"...}}.

SERVER_IP/delete_session/

Delete the chatting session on the back-end server. Send an empty DELETE request. Returns an HttpResponse indicating success or failure.

### AR

SERVER_IP/get_ar_model/

Get the AR model of the product. Refer to SERVER_IP/fetch_product/. Returns the AR model is possible. Returns a failure response if not finding the product or no available AR model.
