[![Build Status](https://travis-ci.org/mercadopago/px-android.svg?branch=master)](https://travis-ci.org/mercadopago/px-android)
[![Codecov branch](https://img.shields.io/codecov/c/github/mercadopago/px-android/develop.svg)](https://codecov.io/gh/mercadopago/px-android/)
[![Bintray](https://img.shields.io/bintray/v/mercadopago/android/com.mercadopago.android.px.checkout.svg)](https://bintray.com/mercadopago/android/com.mercadopago.android.px.checkout)
![GitHub tag](https://img.shields.io/github/tag/mercadopago/px-android.svg)
![GitHub top language](https://img.shields.io/github/languages/top/mercadopago/px-android.svg)


![Screenshot MercadoPago](https://i.imgur.com/ZaqavRJ.jpg)


The MercadoPago Android Payment Experience makes it easy to collect your user's credit card details inside your android app. By creating tokens, MercadoPago handles the bulk of PCI compliance by preventing sensitive card data from hitting your server.


### 🌟 Features

- Easy to install

- Easy to integrate

- PCI compliance

- Basic color customization

- Advanced color customization
 
- Lazy loading initialization support

- Custom Fragments support in certain screens

- Support to build your own Payment Processor

- Support to create your own custom Payment Method

## Installation

### Android Studio

Add this line to your app's `build.gradle` inside the `dependencies` section:

    implementation 'com.mercadopago.android:px-checkout:4.0.0'
    

## 🐒 How to use?

Only **3** steps needed to create a basic checkout using `MercadoPagoCheckout`:

1) Import into your project
```java
import com.mercadopago.android.px.core.MercadoPagoCheckout.Builder;
```

2) Set your PublicKey and PreferenceId
```java
final MercadoPagoCheckout checkout = new MercadoPagoCheckout.Builder("your_public_key", "your_checkout_preference_id")
    .build();
```

3) Start
```java
checkout.startPayment(activityOrContext, requestCode);
```

### One line integration
```java
new MercadoPagoCheckout.Builder("your_public_key", "your_checkout_preference_id")
    .build()
    .startPayment(activityOrContext, requestCode);
```

### Advanced integration
Check our official code [reference](http://mercadopago.github.io/px-android/), especially ```MercadoPagoCheckoutBuilder``` object to explore all available functionalities.

### 🔮 Project Example
This project include an example project using MercadoPago PX. In case you need support contact the MercadoPago Developers Site.


## Documentation

+ [See the GitHub project.](https://github.com/mercadopago/px-android)
+ [See our Github Page's Documentation!](http://mercadopago.github.io/px-android/)
+ [Check out MercadoPago Developers Site!](http://www.mercadopago.com.ar/developers)

## Feedback

You can join the MercadoPago Developers Community on MercadoPago Developers Site:

+ [English](https://www.mercadopago.com.ar/developers/en/community/forum/)
+ [Español](https://www.mercadopago.com.ar/developers/es/community/forum/)
+ [Português](https://www.mercadopago.com.br/developers/pt/community/forum/)

## 🌈 Color customization
```
    <!-- Toolbar and status bar default -->
	<color name="px_colorPrimary">@color/ui_components_android_color_primary</color>
	<color name="px_colorPrimaryDark">@color/ui_components_android_color_primary_dark</color>
	<color name="px_colorAccent">@color/ui_components_android_color_accent</color>
	<color name="px_background">@color/px_colorPrimary</color>
	<color name="px_toolbar_text">@color/ui_components_white_color</color>

	<!-- Discount modal and discount description -->
	<color name="px_discount_summary_background">#474747</color>
	<color name="px_discount_description">#64c574</color>

	<!-- Inputs -->
	<color name="px_input">@color/px_colorPrimary</color>

	<!-- R&C toolbar -->
	<color name="px_review_background">@color/ui_components_white_color</color>
	<color name="px_review_toolbar_text">@color/px_colorPrimary</color>

	<!-- R&C summary -->
	<color name="px_review_summary_background">@color/px_white</color>
	<color name="px_summary_text_color">@color/ui_components_dark_grey_color</color>
	<color name="px_summary_separator_color">@color/px_separator</color>

	<!-- R&C components -->
	<color name="px_review_payment_method_background">@color/px_light_gray</color>
	<color name="px_review_item_background">@color/px_light_gray</color>

	<!-- Spinner -->
	<color name="px_background_loading">@color/ui_components_white_color</color>
	<color name="px_tint_loading">@color/px_colorPrimary</color>

	<color name="px_paymentMethodTint">@color/px_colorPrimary</color>
```
## 🌈 Fonts customization

Our checkout uses REGULAR and LIGHT fonts declared here:

[Meli UI](https://github.com/mercadolibre/fury_mobile-android-ui/blob/release/5.6/ui/src/main/java/com/mercadolibre/android/ui/font/Font.java)

```
Fonts.setFonts(yourFontsPathsByType)
```

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
