E-Commerce Backend API
Project Description
A robust RESTful backend service designed for a simple e-commerce platform. This system manages the entire shopping lifecycle, including product inventory, shopping cart management, order processing, and secure payment integration using Razorpay. The application is built with Java Spring Boot and uses MongoDB for data persistence.

Technology Stack
Language: Java 17

Framework: Spring Boot

Database: MongoDB

Payment Gateway: Razorpay

Build Tool: Maven

Key Features
Product Management

Create new products with price, description, and stock levels.

Retrieve a list of all available products.

Shopping Cart

Add items to a user-specific cart.

Automatic validation to ensure requested quantity does not exceed available stock.

View current cart contents or clear the cart.

Order Processing

Convert cart items into a formal order.

Automatically calculate total amounts.

Deduct purchased quantities from product inventory upon order creation to prevent overselling.

Payment Integration

Generate payment links linked to specific order IDs.

Secure Webhooks: Listens for Razorpay payment events (payment.captured, payment.failed) and verifies the cryptographic signature (HMAC-SHA256) before updating order status in the database.

API Endpoints
Products

POST /api/products - Create a new product.

GET /api/products - Retrieve all products.

Cart

POST /api/cart/add - Add an item to the cart (Params: userId, productId, quantity).

GET /api/cart/{userId} - Get cart items for a user.

DELETE /api/cart/{userId}/clear - Remove all items from a user's cart.

Orders

POST /api/orders - Create an order from the current cart (Param: userId).

GET /api/orders/{orderId} - Retrieve order details.

Payments & Webhooks

POST /api/payments/create - Initialize a payment record (Params: orderId, amount).

POST /api/webhooks/razorpay/payment - Endpoint for Razorpay to send payment status updates.

Configuration & Setup
Prerequisites:

Ensure MongoDB is installed and running on port 27017.

Java JDK 17 or higher.

Application Properties: The application is pre-configured with test credentials in src/main/resources/application.properties:

razorpay.key_id: (configured)

razorpay.key_secret: (configured)

spring.data.mongodb.uri: mongodb://localhost:27017/ecommerce

Running the Application: Use the included Maven wrapper to run the application:

Bash
./mvnw spring-boot:run
Testing Interface
A standalone HTML file is included to test the full flow without needing a frontend app.

File: payment-flow-test.html

Usage: Open this file in any web browser. It contains buttons and forms to simulate the entire user journey:

Create a Product

Add to Cart

Create Order

Simulate Payment & Webhook Response
