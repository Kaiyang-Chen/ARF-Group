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

### Homepage product browsing

SERVER_IP/fetch_home_products/

Fetch 5-10 products' information that is recommended for the user based on his browsing history. Sends a GET request with an empty body. Returns an HttpResponse containing products' name, price, photo and UID. E.g., {"UID1":{"name":"chair","price":32,"photo":"photo_url"...},"UID2":...} Returns a failure response if not logging in or internal error.

### Searching page

SERVER_IP/fetch_searched_products/

Fetch products' information that is satisfied by the searching condition. Sends a GET request with a body containing the conditions and wanted fields. E.g., {"condition":{"type":"chair","highest price":"32"...},"field":{"UID":"","owner":""...}} Returns an HttpResponse containing required products' information Returns a failure response if not logging in or internal error.

### Detailed product information

SERVER_IP/fetch_product/

Fetch all information about the product. Send a GET request with a body including its UID. E.g., {"UID":"12323"} Returns an HttpResponse containing all information Returns a failure response if not logging in or internal error.

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

### Products management

SERVER_IP/post_product/

Try to POST a new product with product name. Additional fields may be provided. Otherwise, these fields will be default values. E.g., {"name":"nice chair", "price":32}. Pictures of the product will be sent via url.
Returns a HttpResponse indicating sucess (if so, its UID will be returned) or failure. An additional HttpRequest from Model Genetor will be sent when AR generation is finished. Subject to change.


SERVER_IP/update_product/

Try to POST a new product with product UID. Fields to be updated and the new values should be provided. E.g., {"UID":"12345", "price":32}. Pictures of the product will be sent via url.
Returns a HttpResponse indicating sucess or failure. An additional HttpRequest from Model Genetor will be sent when AR generation is required and finished. Subject to change.

# View UI/UX

# Team Roster

Kaiyang Chen

Fan Chen

Xinmiao Yu

Zhengyang Zhu

Xingyuan Wang

Weikai Zhou

# Individual Contributions

Kaiyang Chen

Fan Chen

Xinmiao Yu

Zhengyang Zhu

Xingyuan Wang
Developed the backend parts for the following features:
User system: register, login/logout, update user profile, check user profiles, delete user
Browse furniture: search for furniture, get homepage recommendation, get the breif infomation of products, get the detailed information of products
Publish furniture: post products, post pictures of the product, delete the product, update the product information, get the product picture
Chatting system (only available in the backend for now): post text chats, post pictures, get messages
Helped with frontend debugging.

Weikai Zhou
