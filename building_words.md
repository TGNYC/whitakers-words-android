A compiled version of words 1.97FC is included in assets/words. This is because android ndk does not support Ada, nor do most cross compiler toolchains.

Compilation procedure:

- Download and extract the [source code](http://archives.nd.edu/whitaker/wordsall.zip)
- Set up an environment that allows compilation of Ada code for ARM processors. Setting up a cross-compiler is the best method, but is problematic since most prebuilt toolchains do not include Ada support, so you have to compile it yourself. I managed to do this, but it is also possible (and perhaps easier) to set up an ARM chroot using qemu.
- Run the following command:

```bash
gnatmake -O3 words -bargs -static -largs -static
gnatmake makedict
gnatmake makestem
gnatmake makeefil
gnatmake makeinfl
echo G | ./makedict
echo G | ./makestem
echo G | ./makeefil
echo G | ./makeinfl
```

- You can then copy all the required files into assets/words.
