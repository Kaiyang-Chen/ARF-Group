# AR-enabled-furniture-trading-plateform
Capstone project (VE441 App Dev for Entrepreneurs) for SJTU-UM Joint Institute

## Build

Backend
- Django 4.0.4
- PostgreSQL 14.3

Frontend
- Android API level 30
- Andriod Studio
- ARCore SDK for Android 1.31.0
- Android Emulator Pixel 4 API 29 (The app may not work on other emulators)

## Model and Engine

### Storymap

#### Buyers
![graph](readme_graphs/buyers.png "Buyers")

#### Sellers
![graph](readme_graphs/sellers.png "Sellers")

### Data and Control Blocks
![graph](readme_graphs/block.png "Sellers")

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

SERVER_IP/update/

Update the user's information. A POST request with a body containing the required fields. E.g., {"username":"new_name", "gender":"male"}

SERVER_IP/login/

Try to log in a session with user name and password. E.g., {"username":"cky", "password":"1234567"}

SERVER_IP/register/

Try register a new user with user name and password. Additional fields may be provided. Otherwise, these fields will be default values. E.g., {"username":"cky", "password":"1234567", "address":"xxx"}

SERVER_IP/logout/

Log out.

SERVER_IP/delete/

Delete the current user.

### Browser

SERVER_IP/fetch_home_products/

Get at most 64 UID for homepage.

send: anything

receive: {"0": "13233412-34xewrcr", "1": "7645ytewerg-vewegf"}

SERVER_IP/fetch_searched_products/

Get at most 64 uids by searching.

send: {"keywords": "sofa big", "owner": "xingyanwan", "primary_class": "living room", "secondary_class": "sofa", "color_style": "blue", "price_gt": "100", "price_lt": "500", "starts_from": "128"}

starts_from: for example, if you have checked 64 results and want to see more results, set starts_from = 64

receive: {"0": "13233412-34xewrcr", "1": "7645ytewerg-vewegf"}

SERVER_IP/fetch_product_brief/

Get the brief info by UID.

send: {"UID": "afrtr-43gtwwf"}

receive: {"name": "good sofa", "description": "this is a sofa", "price": 200, "picture": "some url"}

picture is the url of the title page, the name of this picture is title.jpg in FS

SERVER_IP/fetch_product_detailed/

Get all the information except AR model, plus user contact information (added).

send: {"UID": "afrtr-43gtwwf"}

receive: {"UID": prod.id, "name": prod.name, "description": prod.description,
"owner": username, "primary_class": prod.primary_class,
"secondary_class": prod.secondary_class, "color_style": prod.color_style,
"price": prod.price, "sold_state": prod.sold_state,
"picture_0": url0, "picture_1": url1}

all pictures will be sent, test.jpg on FS will result in "test": some_url

primary_class is level-1 classification like kitchen, living room, bathroom; for recommendation and searching

secondary_class is level-2 classfication like sofa, chair; for recommendation and searching

### Publisher

SERVER_IP/post_product/

Post a product, without picture.

must login first with cookie in the request

send: {"name": prod.name, "description": prod.description, "primary_class": prod.primary_class,
"secondary_class": prod.secondary_class, "color_style": prod.color_style, "price": prod.price}

receive: HttpResponse starts with failed or {"UID": "tw5y65we3t4sdv"}

SERVER_IP/post_picture/

Post a picture.

must login first with cookie in the request, you must be the owner

send: {"UID": "wdefargstrdtyu", "picture": "pic_name", "image": img_file}

like lab2 request.FILES["image"] has the file

on FS, the new picture will be named as "pic_name.jpg"

name it "title" if you want it as the result picture in fetch_product_brief

if "pic_name.jpg" exists, it will be overwritten

SERVER_IP/delete_picture/

Delete a picture.

must login first with cookie in the request, you must be the owner
POST method

send: {"UID": "wdefargstrdtyu", "picture": "pic_name"}

SERVER_IP/update_product/

Refer to post_product, except UID must be provided.

SERVER_IP/delete_product/

Delete a product.

must login first with cookie in the request, you must be the owner

send: {"UID": "wdefargstrdtyu"}

### Chat

SERVER_IP/post_chat/

Post a chatting message.

Indicate "seller" or "buyer" in the sending message, and the other field will be your username.

send: {"seller":"wxy", "content":"hello"}

SERVER_IP/post_chat_picture/

Post a chatting picture.

send: {"buyer":"wxy"} and some picture

SERVER_IP/get_message/

Get chatting messages.

send: (optional body) {"last_idx":9}

"last_idx" means you do not want to fetch information from the beginning

receives: {"as_seller":{{"0":{"seller":wxy,"buyer":"cf","content":"hello","is_picture":"False"}}},as_buyer:{{"0":{"seller":cf,"buyer":"wxy","content":"hello2","is_picture":"False"}}}}

if is_picture, content will be picture url

### AR

SERVER_IP/get_ar_model/

Get the AR model of the product. Refer to SERVER_IP/fetch_product/. Returns the AR model is possible. Returns a failure response if not finding the product or no available AR model.

### Purchase

SERVER_IP/buy_product/

Try to buy the product. Send a GET request with a body including its UID. E.g., {"UID":"12323"} Returns an HttpResponse indicating success or failure.

# View UI/UX

The picture below shows our static flow of UIUX.

![staticflow](readme_graphs/staticflow.png)

For User System, user should be able to click "Me" and check their basic infromation on the Me page. They can also update their information as needed. All the personal information gathered together can make it easier for users to check and update.

![Usersystem](readme_graphs/Usersystem.png)

For Publish System, the user can click on the "+" button and add basic information, upload pictures. With the pictures uploaded, our app will generated 3D model and the user can preview the model in AR view before publish.

![publish](readme_graphs/publish.png)

For Product Detail, the users can click on the furniture shown on the homepage or search page to find the details. The contact information of the seller is provided for them to communication. We may even provide built-in chatting function for them to communicate. Buyers can also view the 3D model of the furniture to get a better understanding of it.

![detail](readme_graphs/detail.png)

For Searching Product, the users can click on the search icon and enter the keywords to search and view related furniture. This function can make it easier for users to find the furniture they want.

![search](readme_graphs/search.png)

For Shopping Cart, this the page where users can check out. It shows the furniture users want to buy and provide the payment function to pay the bill.

![shop](readme_graphs/shop.png)

# Team Roster

Kaiyang Chen

Fan Chen

Xinmiao Yu

Zhengyang Zhu

Xingyuan Wang

Weikai Zhou

# Individual Contributions

#### Kaiyang Chen




#### Fan Chen

Developed the frontend parts for the following features:

User system: register, part of login, update user profile, check user profiles

Publish furniture: post products, post pictures of the product



#### Xinmiao Yu

Developed the AR view part. User could enter AR view and virtually put one default model in the environment.


#### Zhengyang Zhu

Developed the frontend product detail page.




#### Xingyuan Wang

Developed the backend parts for the following features:

User system: register, login/logout, update user profile, check user profiles, delete user

Browse furniture: search for furniture, get homepage recommendation, get the breif infomation of products, get the detailed information of products

Publish furniture: post products, post pictures of the product, delete the product, update the product information, get the product picture

Chatting system (only available in the backend for now): post text chats, post pictures, get messages

Helped with frontend debugging.




#### Weikai Zhou

Developed the frontend parts for the following features:

Home, Search, Publish, Cart and Me nevigation structure of the APP

Implementation of HomePage and connection between HomePage and DetailPage
