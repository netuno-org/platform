---
layout: docs
title: Typography
description: Documentation and examples for Boosted typography, including global settings, headings, body text, lists, and more.
group: content
toc: true
---

## Global settings

Boosted sets basic global display, typography, and link styles. When more control is needed, check out the [textual utility classes]({{ site.baseurl }}/docs/{{ site.docs_version }}/utilities/text/).

- Use a [native font stack]({{ site.baseurl }}/docs/{{ site.docs_version }}/content/reboot/#native-font-stack) that selects the best `font-family` for each OS and device.
- For a more inclusive and accessible type scale, we assume the browser default root `font-size` (typically 16px) so visitors can customize their browser defaults as needed.
- Use the `$font-family-base`, `$font-size-base`, and `$line-height-base` attributes as our typographic base applied to the `<body>`.
- Set the global link color via `$link-color` and apply link underlines only on `:hover`.
- Use `$body-bg` to set a `background-color` on the `<body>` (`#fff` by default).

These styles can be found within `_reboot.scss`, and the global variables are defined in `_variables.scss`. Make sure to set `$font-size-base` in `rem`.

## Headings

All HTML headings, `<h1>` through `<h6>`, are available.

<table>
  <!-- BOOSTED MOD -->
  <thead>
    <tr>
    <th scope="col">Heading</th>
    <th scope="col">Computer size</th>
    <th scope="col">Mobile size</th>
    </tr>
  </thead>
  <!-- END MOD -->    
  <tbody>
    <tr>
      <td>{{ "`<h1></h1>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 2.125rem (34px)</td>
      <td class="type-info">Bold 1.25rem (20px)</td>
      <!-- END MOD -->
    </tr>
    <tr>
      <td>{{ "`<h2></h2>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 1.875rem (30px)</td>
      <td class="type-info">Bold 1.125rem (18px)</td>
      <!-- END MOD -->
    </tr>
    <tr>
      <td>{{ "`<h3></h3>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 1.5rem (24px)</td>
      <td class="type-info">Bold 1rem (16px)</td>
      <!-- END MOD -->
    </tr>
    <tr>
      <td>{{ "`<h4></h4>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 1.25rem (20px)</td>
      <td class="type-info">Bold .875rem (14px)</td>
      <!-- END MOD -->
    </tr>
    <tr>
      <td>{{ "`<h5></h5>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 1.125rem (18px)</td>
      <td class="type-info">Bold .875rem (14px)</td>
      <!-- END MOD -->
    </tr>
    <tr>
      <td>{{ "`<h6></h6>`" | markdownify }}</td>
      <!-- BOOSTED MOD -->
      <td class="type-info">Bold 1rem (16px)</td>
      <td class="type-info">Bold .875rem (14px)</td>        
      <!-- END MOD -->
    </tr>
  </tbody>
</table>

{% highlight html %}
<h1>h1. Boosted heading</h1>
<h2>h2. Boosted heading</h2>
<h3>h3. Boosted heading</h3>
<h4>h4. Boosted heading</h4>
<h5>h5. Boosted heading</h5>
<h6>h6. Boosted heading</h6>
{% endhighlight %}

`.h1` through `.h6` classes are also available, for when you want to match the font styling of a heading but cannot use the associated HTML element.

{% capture example %}
<p class="h1">h1. Boosted heading</p>
<p class="h2">h2. Boosted heading</p>
<p class="h3">h3. Boosted heading</p>
<p class="h4">h4. Boosted heading</p>
<p class="h5">h5. Boosted heading</p>
<p class="h6">h6. Boosted heading</p>
{% endcapture %}
{% include example.html content=example %}

### Customizing headings

Use the included utility classes to recreate the small secondary heading text from Boosted 3.

<div class="bd-example">
  <span class="h3">
    Fancy display heading
    <small class="text-muted">With faded secondary text</small>
  </span>
</div>

{% highlight html %}
<h3>
  Fancy display heading
  <small class="text-muted">With faded secondary text</small>
</h3>
{% endhighlight %}

## Display headings

Traditional heading elements are designed to work best in the meat of your page content. When you need a heading to stand out, consider using a **display heading**—a larger, slightly more opinionated heading style.

<div class="bd-example bd-example-type">
  <table class="table">
    <tbody>
      <tr>
        <td><span class="display-1">Display 1</span></td>
      </tr>
      <tr>
      <td><span class="display-2">Display 2</span></td>
      </tr>
      <tr>
      <td><span class="display-3">Display 3</span></td>
      </tr>
      <tr>
      <td><span class="display-4">Display 4</span></td>
      </tr>
    </tbody>
  </table>
</div>

{% highlight html %}
<h1 class="display-1">Display 1</h1>
<h1 class="display-2">Display 2</h1>
<h1 class="display-3">Display 3</h1>
<h1 class="display-4">Display 4</h1>
{% endhighlight %}

## Lead

Make a paragraph stand out by adding `.lead`.

{% capture example %}
<p class="lead">
  Vivamus sagittis lacus vel augue laoreet rutrum faucibus dolor auctor. Duis mollis, est non commodo luctus.
</p>
{% endcapture %}
{% include example.html content=example %}

## Inline text elements

Styling for common inline HTML5 elements.

{% capture example %}
<p>You can use the mark tag to <mark>highlight</mark> text.</p>
<p><del>This line of text is meant to be treated as deleted text.</del></p>
<p><s>This line of text is meant to be treated as no longer accurate.</s></p>
<p><ins>This line of text is meant to be treated as an addition to the document.</ins></p>
<p><u>This line of text will render as underlined</u></p>
<p><small>This line of text is meant to be treated as fine print.</small></p>
<p><strong>This line rendered as bold text.</strong></p>
<p><em>This line rendered as italicized text.</em></p>
{% endcapture %}
{% include example.html content=example %}

`.mark` and `.small` classes are also available to apply the same styles as `<mark>` and `<small>` while avoiding any unwanted semantic implications that the tags would bring.

While not shown above, feel free to use `<b>` and `<i>` in HTML5. `<b>` is meant to highlight words or phrases without conveying additional importance while `<i>` is mostly for voice, technical terms, etc.

## Text utilities

Change text alignment, transform, style, weight, and color with our [text utilities]({{ site.baseurl }}/docs/{{ site.docs_version }}/utilities/text/) and [color utilities]({{ site.baseurl }}/docs/{{ site.docs_version }}/utilities/colors/).

## Abbreviations

Stylized implementation of HTML's `<abbr>` element for abbreviations and acronyms to show the expanded version on hover. Abbreviations have a default underline and gain a help cursor to provide additional context on hover and to users of assistive technologies.

Add `.initialism` to an abbreviation for a slightly smaller font-size.

{% capture example %}
<p><abbr title="attribute">attr</abbr></p>
<p><abbr title="HyperText Markup Language" class="initialism">HTML</abbr></p>
{% endcapture %}
{% include example.html content=example %}

## Blockquotes

For quoting blocks of content from another source within your document. Wrap `<blockquote class="blockquote">` around any <abbr title="HyperText Markup Language">HTML</abbr> as the quote.

{% capture example %}
<blockquote class="blockquote">
  <p class="mb-0">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
</blockquote>
{% endcapture %}
{% include example.html content=example %}

### Naming a source

Add a `<footer class="blockquote-footer">` for identifying the source. Wrap the name of the source work in `<cite>`.

{% capture example %}
<blockquote class="blockquote">
  <p class="mb-0">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
  <footer class="blockquote-footer">Someone famous in <cite title="Source Title">Source Title</cite></footer>
</blockquote>
{% endcapture %}
{% include example.html content=example %}

### Alignment

Use text utilities as needed to change the alignment of your blockquote.

{% capture example %}
<blockquote class="blockquote text-center">
  <p class="mb-0">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
  <footer class="blockquote-footer">Someone famous in <cite title="Source Title">Source Title</cite></footer>
</blockquote>
{% endcapture %}
{% include example.html content=example %}

{% capture example %}
<blockquote class="blockquote text-right">
  <p class="mb-0">Lorem ipsum dolor sit amet, consectetur adipiscing elit. Integer posuere erat a ante.</p>
  <footer class="blockquote-footer">Someone famous in <cite title="Source Title">Source Title</cite></footer>
</blockquote>
{% endcapture %}
{% include example.html content=example %}

## Lists

### Unstyled

Remove the default `list-style` and left margin on list items (immediate children only). **This only applies to immediate children list items**, meaning you will need to add the class for any nested lists as well.

{% capture example %}
<ul class="list-unstyled">
  <li>Lorem ipsum dolor sit amet</li>
  <li>Consectetur adipiscing elit</li>
  <li>Integer molestie lorem at massa</li>
  <li>Facilisis in pretium nisl aliquet</li>
  <li>Nulla volutpat aliquam velit
    <ul>
      <li>Phasellus iaculis neque</li>
      <li>Purus sodales ultricies</li>
      <li>Vestibulum laoreet porttitor sem</li>
      <li>Ac tristique libero volutpat at</li>
    </ul>
  </li>
  <li>Faucibus porta lacus fringilla vel</li>
  <li>Aenean sit amet erat nunc</li>
  <li>Eget porttitor lorem</li>
</ul>
{% endcapture %}
{% include example.html content=example %}

### Inline

Remove a list's bullets and apply some light `margin` with a combination of two classes, `.list-inline` and `.list-inline-item`.

{% capture example %}
<ul class="list-inline">
  <li class="list-inline-item">Lorem ipsum</li>
  <li class="list-inline-item">Phasellus iaculis</li>
  <li class="list-inline-item">Nulla volutpat</li>
</ul>
{% endcapture %}
{% include example.html content=example %}

### Description list alignment

Align terms and descriptions horizontally by using our grid system's predefined classes (or semantic mixins). For longer terms, you can optionally add a `.text-truncate` class to truncate the text with an ellipsis.

{% capture example %}
<dl class="row">
  <dt class="col-sm-3">Description lists</dt>
  <dd class="col-sm-9">A description list is perfect for defining terms.</dd>

  <dt class="col-sm-3">Euismod</dt>
  <dd class="col-sm-9">
    <p>Vestibulum id ligula porta felis euismod semper eget lacinia odio sem nec elit.</p>
    <p>Donec id elit non mi porta gravida at eget metus.</p>
  </dd>

  <dt class="col-sm-3">Malesuada porta</dt>
  <dd class="col-sm-9">Etiam porta sem malesuada magna mollis euismod.</dd>

  <dt class="col-sm-3 text-truncate">Truncated term is truncated</dt>
  <dd class="col-sm-9">Fusce dapibus, tellus ac cursus commodo, tortor mauris condimentum nibh, ut fermentum massa justo sit amet risus.</dd>

  <dt class="col-sm-3">Nesting</dt>
  <dd class="col-sm-9">
    <dl class="row">
      <dt class="col-sm-4">Nested definition list</dt>
      <dd class="col-sm-8">Aenean posuere, tortor sed cursus feugiat, nunc augue blandit nunc.</dd>
    </dl>
  </dd>
</dl>
{% endcapture %}
{% include example.html content=example %}

[comment]: # Boosted mod
### Orange list with bullet points

Simply add `o-square-list` class to the root element of a list (`<ul>`). Maximum elements depth is 3.

{% capture example %}
<ul class="o-square-list">
    <li>
        <span>first</span>
        <ul>
            <li>
                <span>1.1</span>
                <ul>
                    <li><span>1.1.1</span></li>
                    <li><span>1.1.2</span></li>
                </ul>
            </li>
            <li>
                <span>1.2</span>
            </li>
        </ul>
    </li>
    <li>
        <span>second</span>
    </li>
    <li>
        <span>third</span>
    </li>
    <li>
        <span>fourth</span>
    </li>
</ul>
{% endcapture %} {% include example.html content=example %}

## Links

By default, links are black, and not underlined

{% capture example %}
<a href="#">This is a sample default link</a>
{% endcapture %} {% include example.html content=example %}

### Underlined

A link into a `p` tag become underlined, to be clearly identified.

{% capture example %}
<p>Some text in a paragraph, and so <a href="#">the link are underline</a></p>
{% endcapture %} {% include example.html content=example %}

### With arrow

Add the class `o-link-arrow` to a link, add an arrow after the text link.

{% capture example %}
<a href="#" class="o-link-arrow">This is a sample link with arrow</a>
{% endcapture %} {% include example.html content=example %}

### With back arrow

To convert arrow link to backward link, add `back` class to the link with class `o-link-arrow`.

{% capture example %}
<a href="#" class="o-link-arrow back">This is a sample link with arrow for backward</a>
{% endcapture %} {% include example.html content=example %}

[comment]: # end mod

## Responsive typography

*Responsive typography* refers to scaling text and components by simply adjusting the root element's `font-size` within a series of media queries. Boosted doesn't do this for you, but it's fairly easy to add if you need it.

Here's an example of it in practice. Choose whatever `font-size`s and media queries you wish.

{% highlight scss %}
html {
  font-size: 1rem;
}

@include media-breakpoint-up(sm) {
  html {
    font-size: 1.2rem;
  }
}

@include media-breakpoint-up(md) {
  html {
    font-size: 1.4rem;
  }
}

@include media-breakpoint-up(lg) {
  html {
    font-size: 1.6rem;
  }
}
{% endhighlight %}
