This fellow program strips out CSS, images and background colors from incoming html emails. Designed to work as a Dovecot + Sieve filter. Graalvm-compilable. The beauty of its work is depicted in the propaganda folder.

WHY

Html emails are high-cortisol cause of using too much styling and tendin to hide the unsubscribe button. So if I need to keep my mind peaceful dealin with high amounts of different emails, I need them simple. Text must be black on white and the unsub link must be blue and underlined. I’m using  built-in mail clients, not the geeky ones. So the client-side de-htmlin is limited and can’t be trusted. 

There are different approaches in server-side de-htmlin emails. 

First, most of multipart emails do come with a plain text part. I could just strip out the whole html part and keep the text, but the text part is usually hard to read, poorly formatted and contains lots of long marketing links. So I decided not to touch that. 

Second, there are lots of html —> plain text formatters. Them can’t be used either cause they don’t preserve links. 

So my naive approach was to just delete all css, colors and imgs. And see what comes out. The outcome was so simply beautiful for the most emails that I decided to keep it. 

Emails can be multipart, also nested, so the program recursively parses an email, searchin for the html part. Then it decodes the part, removes the design, encodes it back and puts it where the original was. All other parts and the headers are left untouched. 

All the email manipulations done via Java interop, including javax mail. So it supports all the encodings and the parsing is, I hope, robust. 

USAGE

First, you gon need to create a wrapper:

sudo tee /usr/local/bin/strip-css << 'EOF'
#!/bin/bash
/usr/local/bin/mail-stripper

Then, the Sieve rule:

require ["vnd.dovecot.filter"];

if true {
    filter "mail-stripper";
}
