---
layout: docs
title: Introduction
description: Get started with Boosted using the Boosted source and a template starter page.
group: getting-started
redirect_from:
  - /docs/
  - /docs/4.1/
  - /docs/4.1/getting-started/
  - /docs/getting-started/
toc: true
---

## About Orange Brand

All Boosted components are compliant with Orange Brand Guidelines. Some Boosted features may not support the brand guidelines, partially or entirely. They are tagged in this documentation with the following <span class="sr-only">warning, not brand compliant</span><span class="icon-anti-spam" style="color: #dc3c14; font-size: 2rem" aria-hidden="true"></span>, use them at your own risks. Please note the entire Boosted doc is preserved and enhancend when necessary with orange brand specifics, i.e. [navbar](../../components/navbar/) page contains unsupported features, check out [navbar orange](../../components/navbar-orange/) page to find out what to use.

## Quick start

Once you have [downloaded](../download/) boosted source files, copy-paste the stylesheet `<link>` into your `<head>` before all other stylesheets to load our CSS. You can also add orangeHelvetica and orangeIcons if needed, be careful both files are under copyright, see NOTICE.txt for more information.

{% highlight html %}
<!-- Copyright © 2014 Monotype Imaging Inc. All rights reserved -->
<link rel="stylesheet" href="path/to/your/orangeHelvetica.css">
<!-- Copyright © 2016 Orange SA. All rights reserved -->
<link rel="stylesheet" href="path/to/your/orangeIcons.css">
<link rel="stylesheet" href="path/to/your/boosted.css">
{% endhighlight %}

### JS

Many of our components require the use of JavaScript to function. Specifically, they require [jQuery](https://jquery.com), [Popper.js](https://popper.js.org/), and our own JavaScript plugins. Place the following `<script>`s near the end of your pages, right before the closing `</body>` tag, to enable them. jQuery must come first, then Popper.js, and then our JavaScript plugins.

We use [jQuery's slim build](https://blog.jquery.com/2016/06/09/jquery-3-0-final-released/), but the full version is also supported.

{% highlight html %}
<script src="{{ site.cdn.jquery }}" integrity="{{ site.cdn.jquery_hash }}" crossorigin="anonymous"></script>
<!-- if you need ajax or effects
<script src="{{ site.cdn.jquery_full }}" integrity="{{ site.cdn.jquery_full_hash }}" crossorigin="anonymous"></script>
-->
<script src="{{ site.cdn.popper }}" integrity="{{ site.cdn.popper_hash }}" crossorigin="anonymous"></script>
<script src="path/to/your/boosted.js"></script>
{% endhighlight %}

Curious which components explicitly require jQuery, our JS, and Popper.js? Click the show components link below. If you're at all unsure about the general page structure, keep reading for an example page template.

Our `boosted.bundle.js` and `boosted.bundle.min.js` include [Popper](https://popper.js.org/), but not [jQuery](https://jquery.com/). For more information about what's included in Boosted, please see our [contents]({{ site.baseurl }}/docs/{{ site.docs_version }}/getting-started/contents/#precompiled-boosted) section.

<details>
<summary class="text-primary mb-3">Show components requiring JavaScript</summary>
{% capture markdown %}
- Alerts for dismissing
- Buttons for toggling states and checkbox/radio functionality
- Carousel for all slide behaviors, controls, and indicators
- Collapse for toggling visibility of content
- Dropdowns for displaying and positioning (also requires [Popper.js](https://popper.js.org/))
- Modals for displaying, positioning, and scroll behavior
- Navbar for extending our Collapse plugin to implement responsive behavior
- Tooltips and popovers for displaying and positioning (also requires [Popper.js](https://popper.js.org/))
- Scrollspy for scroll behavior and navigation updates
{% endcapture %}
{{ markdown | markdownify }}
</details>

## Starter template

Be sure to have your pages set up with the latest design and development standards. That means using an HTML5 doctype and including a viewport meta tag for proper responsive behaviors. Put it all together and your pages should look like this:

{% highlight html %}
<!doctype html>
<html lang="en">
  <head>
    <!-- Required meta tags -->
    <meta charset="utf-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
    <meta http-equiv="x-ua-compatible" content="ie=edge">

    <!--
      Neue Helvetica is a trademark of Monotype Imaging Inc. registered in the U.S.
      Patent and Trademark Office and may be registered in certain other jurisdictions.
      Copyright © 2014 Monotype Imaging Inc. All rights reserved.
      Orange Company had buy the right for used Helvetica onto digital applications.
      If you are not autorized to used it, don't include the orangeHelvetica.css
      See NOTICE.txt for more informations.
    -->
    <link rel="stylesheet" href="css/orangeHelvetica.css" />
    <!--
      Orange Icons
      Copyright (C) 2016 - 2018 Orange SA All rights reserved
      See NOTICE.txt for more informations.
    -->
    <link rel="stylesheet" href="css/orangeIcons.css" />

    <!-- Boosted CSS -->
    <link rel="stylesheet" href="path/to/your/boosted.css">

    <title>Hello, world!</title>
  </head>
  <body>
    <h1>Hello, world!</h1>

    <main id="content" role="main">
      My page content start here
    </main>

    <!-- Optional JavaScript -->
    <!-- jQuery first, then Popper.js, then Boosted JS. -->
    <script src="{{ site.cdn.jquery }}" integrity="{{ site.cdn.jquery_hash }}" crossorigin="anonymous"></script>
    <script src="{{ site.cdn.popper }}" integrity="{{ site.cdn.popper_hash }}" crossorigin="anonymous"></script>
    <script src="path/to/your/boosted.js"></script>
  </body>
</html>
{% endhighlight %}

That's all you need for overall page requirements. Visit the [Layout docs]({{ site.baseurl }}/docs/{{ site.docs_version }}/layout/overview/) or [our official examples]({{ site.baseurl }}/docs/{{ site.docs_version }}/examples/) to start laying out your site's content and components.

## Important globals

Boosted employs a handful of important global styles and settings that you'll need to be aware of when using it, all of which are almost exclusively geared towards the *normalization* of cross browser styles. Let's dive in.

### HTML5 doctype

Boosted requires the use of the HTML5 doctype. Without it, you'll see some funky incomplete styling, but including it shouldn't cause any considerable hiccups.

{% highlight html %}
<!doctype html>
<html lang="en">
  ...
</html>
{% endhighlight %}

### Responsive meta tag

Boosted is developed *mobile first*, a strategy in which we optimize code for mobile devices first and then scale up components as necessary using CSS media queries. To ensure proper rendering and touch zooming for all devices, **add the responsive viewport meta tag** to your `<head>`.

{% highlight html %}
<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
{% endhighlight %}

You can see an example of this in action in the [starter template](#starter-template).

### Box-sizing

For more straightforward sizing in CSS, we switch the global `box-sizing` value from `content-box` to `border-box`. This ensures `padding` does not affect the final computed width of an element, but it can cause problems with some third party software like Google Maps and Google Custom Search Engine.

On the rare occasion you need to override it, use something like the following:

{% highlight css %}
.selector-for-some-widget {
  box-sizing: content-box;
}
{% endhighlight %}

With the above snippet, nested elements—including generated content via `::before` and `::after`—will all inherit the specified `box-sizing` for that `.selector-for-some-widget`.

Learn more about [box model and sizing at CSS Tricks](https://css-tricks.com/box-sizing/).

### Reboot

For improved cross-browser rendering, we use [Reboot]({{ site.baseurl }}/docs/{{ site.docs_version }}/content/reboot/) to correct inconsistencies across browsers and devices while providing slightly more opinionated resets to common HTML elements.

## Community

Stay up to date on the development of Boosted and reach out to the community with these helpful resources.

- Follow [@getbootstrap on Twitter](https://twitter.com/getbootstrap).
- Read and subscribe to [The Official Bootstrap Blog]({{ site.blog }}).
- Join [the official Slack room]({{ site.slack }}).
- Chat with fellow Bootstrappers in IRC. On the `irc.freenode.net` server, in the `##bootstrap` channel.
- Implementation help may be found at Stack Overflow (tagged [`bootstrap-4`](https://stackoverflow.com/questions/tagged/bootstrap-4)).
- Developers should use the keyword `bootstrap` on packages which modify or add to the functionality of Bootstrap when distributing through [npm](https://www.npmjs.com/browse/keyword/bootstrap) or similar delivery mechanisms for maximum discoverability.

You can also follow [@getbootstrap on Twitter](https://twitter.com/getbootstrap) for the latest gossip and awesome music videos.
