[![Build Status](https://travis-ci.org/mercadopago/px-android.svg?branch=master)](https://travis-ci.org/mercadopago/px-android)
[![Codecov branch](https://img.shields.io/codecov/c/github/mercadopago/px-android/develop.svg)](https://codecov.io/gh/mercadopago/px-android/)
[![Bintray](https://img.shields.io/bintray/v/mercadopago/android/com.mercadopago.android.px.checkout.svg)](https://bintray.com/mercadopago/android/com.mercadopago.android.px.checkout)
![GitHub tag](https://img.shields.io/github/tag/mercadopago/px-android.svg)
![GitHub top language](https://img.shields.io/github/languages/top/mercadopago/px-android.svg)

# MercadoPago - Android Payment Experience

The MercadoPago Android Payment Experience makes it easy to collect your user's credit card details inside your android app. By creating tokens, MercadoPago handles the bulk of PCI compliance by preventing sensitive card data from hitting your server.

![Screenshot MercadoPago](https://i.imgur.com/ZaqavRJ.jpg)

## Installation

### Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    implementation 'com.mercadopago.android:px-checkout:4.0.0'
    

## 🐒 How to use?
Only **1** steps needed to create a basic checkout using `MercadoPagoCheckout`:
```java
new MercadoPagoCheckout.Builder("your_public_key", "your_checkout_preference_id")
    .build()
    .startPayment(activityOrContext, requestCode);
```


## Documentation

+ [See the GitHub project.](https://github.com/mercadopago/px-android)
+ [See our Github Page's Documentation!](http://mercadopago.github.io/px-android/)
+ [Check out MercadoPago Developers Site!](http://www.mercadopago.com.ar/developers)

## Feedback

You can join the MercadoPago Developers Community on MercadoPago Developers Site:

+ [English](https://www.mercadopago.com.ar/developers/en/community/forum/)
+ [Español](https://www.mercadopago.com.ar/developers/es/community/forum/)
+ [Português](https://www.mercadopago.com.br/developers/pt/community/forum/)



## 👨🏻‍💻 Author
Mercado Pago / Mercado Libre

## 👮🏻 License

```
MIT License

Copyright (c) 2018 - Mercado Pago / Mercado Libre

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
