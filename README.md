# AWS RSS Updater

A way to get updates from RSS feeds for free using Amazon's free tier.

## Installation

Download from http://example.com/FIXME.

## Usage

aws-rss-updater pulls the list of sites from S3. 

First we need to initialize everything. Pick a S3 bucket name for state to be
stored in and run the below command.

```bash
boot init -b bucket-name
```

This will provision the necessary resources on AWS.


**Deploy**

Deploys the environment to AWS.

```bash
boot init -b bucket-name
```

**Set email address**

Sets the email address to send feed updates to.

```bash
boot set-email me@toocool.io
```

**Subscribe to a feed**

Subscribe to a feed.

```bash
boot subscribe-feed feed-url
```

**Unsubscribe from a feed**

Unsubscribe from a feed

```bash
boot unsubscribe-feed feed-url
```

## Implementation

- Info is stored in a S3 file
- Downloads urls
- Serialize files with Nippy

## TODO

- Create a CLI for all Boot functions
- Add email templates
- Use mustache templates for email template
- Think about re-writing in CLJS if perf is an issue

## License

Copyright © 2017 Kenny Williams

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
