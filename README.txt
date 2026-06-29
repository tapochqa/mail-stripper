This fellow progam strips out CSS, images and background colors from incoming emails. Designed to work as a Dovecot + Sieve filter. Graalvm-compilable. The beauty of its work is depicted in the propaganda folder.

First, you gon need to create a wrapper:

sudo tee /usr/local/bin/strip-css << 'EOF'
#!/bin/bash
/usr/local/bin/mail-stripper

Then, the Sieve rule:

require ["vnd.dovecot.filter"];

if true {
    filter "mail-stripper";
}

