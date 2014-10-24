#How to use
In order to use this implementation, you have to 

- Implement the interface IRepositoryService (With the required repositories based on your preferred persist technology)
- Extend the listener RepositoryServiceInitializer and add your class to your web.xml
- Extend the listener AbstractNetworkServiceInitializer and add your class to your web.xml
- Add the servlet  ShadowRedirectServlet to your web.xml (e.g. with the url /auth)
- Add the servlet ShadowCallbackServlet to your web.xml (e.g. with the url /auth_callback)

- Add the filter ShadowTokenFilter to your web.xml with the filter mapping pointing to your protected resources
