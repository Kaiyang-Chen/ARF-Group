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
send: {"UID": "wdefargstrdtyu", "picture": "pic_name", "image": img_file}
! like lab2 request.FILES["image"] has the file
! UID and picture are also sent in FILES section in bytes
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