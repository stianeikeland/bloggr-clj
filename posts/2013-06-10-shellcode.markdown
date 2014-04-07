{ :slug "shellcode"
  :title "Decoding shellcode"
  :date "2013-06-10 17:03:45+00:00"
  :tags #{:shellcode :asm}}

------

Nasjonal Sikkerhetsmyndighet (NSM) recently put up a few security challenges on their page as part of a hiring, and was linked to by one of the major norwegian tabloids - [VG](http://www.vg.no/nyheter/innenriks/artikkel.php?artid=10117134).

Most of them looked like boring substitution cipher and so on.. but one sparked my interrest - shellcode.


~~~
31 db f7 e3 68 ff f4 f5 e2 68 fb f5
b0 f8 68 b0 fb fc ff 68 fc f5 e2 f5
68 f5 e2 b0 f6 68 e2 f5 fe f7 68 c6
f9 b0 e4 b9 90 90 90 90 31 0c 04 04
04 3c 1c 75 f7 89 e1 31 c0 b0 04 b2
1c cd 80 b0 01 cd 80
~~~

![No disassemble](/images/2013-06-10-shellcode/3pq91u.jpg)

In intel x86 asm opcode 0x31 is a xor, so just based on the first bytes there's a fair chance this shellcode is intel asm. (also it's the most common platform.. which makes sense for something like this)

Run it through a disassembler to verify, ndisasm - nasm's disassembler works great.

~~~ nasm
; ndisasm -b 32 shellcode
00000000  31DB              xor ebx,ebx
00000002  F7E3              mul ebx
00000004  68FFF4F5E2        push dword 0xe2f5f4ff
00000009  68FBF5B0F8        push dword 0xf8b0f5fb
0000000E  68B0FBFCFF        push dword 0xfffcfbb0
00000013  68FCF5E2F5        push dword 0xf5e2f5fc
00000018  68F5E2B0F6        push dword 0xf6b0e2f5
0000001D  68E2F5FEF7        push dword 0xf7fef5e2
00000022  68C6F9B0E4        push dword 0xe4b0f9c6
00000027  B990909090        mov ecx,0x90909090
0000002C  310C04            xor [esp+eax],ecx
0000002F  0404              add al,0x4
00000031  3C1C              cmp al,0x1c
00000033  75F7              jnz 0x2c
00000035  89E1              mov ecx,esp
00000037  31C0              xor eax,eax
00000039  B004              mov al,0x4
0000003B  B21C              mov dl,0x1c
0000003D  CD80              int 0x80
0000003F  B001              mov al,0x1
00000041  CD80              int 0x80
~~~

Short little program. There's a couple of int 0x80 in there, which usually is a really good indication of linux code - int 0x80 is the interrupt vector used for system calls in linux. There's a good syscalls reference at: [syscalls.kernelgrok.com](http://syscalls.kernelgrok.com/). Whatever currently in the eax register is used for the syscall, meaning that the two syscalls used in this code is 4 (sys_write) and 1 (sys_exit).

Breaking the code down you find:

~~~ nasm
00000000  31DB              xor ebx,ebx
00000002  F7E3              mul ebx

; ebx = 0
; eax = eax * ebx = 0

00000004  68FFF4F5E2        push dword 0xe2f5f4ff
00000009  68FBF5B0F8        push dword 0xf8b0f5fb
0000000E  68B0FBFCFF        push dword 0xfffcfbb0
00000013  68FCF5E2F5        push dword 0xf5e2f5fc
00000018  68F5E2B0F6        push dword 0xf6b0e2f5
0000001D  68E2F5FEF7        push dword 0xf7fef5e2
00000022  68C6F9B0E4        push dword 0xe4b0f9c6
00000027  B990909090        mov ecx,0x90909090

; push some data to the stack
; ecx = 0x90909090

0000002C  310C04            xor [esp+eax],ecx
0000002F  0404              add al,0x4
00000031  3C1C              cmp al,0x1c
00000033  75F7              jnz 0x2c

; // Xor the data pushed to the stack with 0x90909090
; while (al != 28) {
;     stack[al] ^= ecx
;     al += 4;
; }

00000035  89E1              mov ecx,esp
00000037  31C0              xor eax,eax
00000039  B004              mov al,0x4
0000003B  B21C              mov dl,0x1c
0000003D  CD80              int 0x80

; // Print stack data to stdout
; sys_write(fd: ebx = 0, *chars: ecx = stack, count: dl = 28);

0000003F  B001              mov al,0x1
00000041  CD80              int 0x80

; sys_exit(1);
~~~

So it simply xors some data with 0x90909090 and print the result. Python is pretty great for massaging data like this, especially using their struct module (binary packer/unpacker).

~~~ python
>>> data = [0xe4b0f9c6, 0xf7fef5e2, 0xf6b0e2f5, 0xf5e2f5fc, 0xfffcfbb0, 0xf8b0f5fb, 0xe2f5f4ff]
>>> xordata = map(lambda x: x ^ 0x90909090, data)
>>> import struct
>>> struct.pack('7I', *xordata)

'Vi trenger flere kloke hoder'
~~~

While I have no plan of applying, I find small challenges like these (used as a way of sparking interest when hunting applicants) pretty interesting. They have (AFAIK) been pretty popular abroad, but have recently started gaining traction here in Norway as well. Many major webpages have been putting up job offerings as comments in HTML or CSS source files, others have designed challenges like this to attract applicants. The job market here is pretty damn good for technology workers, so maybe it's a good tactic for seperating yourself as a company from the rest - and at the same time act as a filter for applications.
