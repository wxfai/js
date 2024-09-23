package com.xf.js;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class macho {

    public static void main(String[] args) {
        String outputFile = "hello_world_macho";

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            // 写入 Mach-O 文件头，64 位架构
            ByteBuffer header = ByteBuffer.allocate(32);
            header.order(ByteOrder.LITTLE_ENDIAN);
            
            // Magic number for 64-bit Mach-O (0xfeedfacf)
            header.putInt(0xfeedfacf);
            // CPU type (CPU_TYPE_X86_64 = 0x01000007)
            header.putInt(0x01000007);
            // CPU subtype (CPU_SUBTYPE_X86_64_ALL = 0x00000003)
            header.putInt(0x00000003);
            // File type (MH_EXECUTE = 0x00000002, executable file)
            header.putInt(0x00000002);
            // Number of load commands (initially set to 0, placeholder)
            header.putInt(1);
            // Size of load commands (initially set to 0, placeholder)
            header.putInt(80); // Load command size placeholder
            // Flags (e.g., MH_NOUNDEFS = 0x00000001)
            header.putInt(0x00000001);

            // 写入Load Commands
            ByteBuffer loadCommand = ByteBuffer.allocate(80);
            loadCommand.order(ByteOrder.LITTLE_ENDIAN);
            
            // Load command for a simple executable
            // LC_SEGMENT_64 for __TEXT segment
            loadCommand.putInt(0x19); // LC_SEGMENT_64
            loadCommand.putInt(72); // command size
            loadCommand.put("__TEXT".getBytes()); // segment name __TEXT
            loadCommand.putLong(0); // vmaddr (placeholder)
            loadCommand.putLong(0); // vmsize (placeholder)
            loadCommand.putLong(0); // fileoff (placeholder)
            loadCommand.putLong(0); // filesize (placeholder)
            loadCommand.putInt(7); // maxprot (read, write, execute)
            loadCommand.putInt(7); // initprot (read, write, execute)
            loadCommand.putInt(0); // nsects
            loadCommand.putInt(0); // flags
            
            fos.write(header.array());
            fos.write(loadCommand.array());

            // 现在写入代码段，使用系统调用写入 "Hello, World!"
            // 系统调用 `write` 是 0x2000004, `exit` 是 0x2000001
            // 写入汇编代码：
            // mov rax, 0x2000004   ; syscall for write
            // mov rdi, 1           ; file descriptor (stdout)
            // lea rsi, [rip+msg]   ; address of message
            // mov rdx, 13          ; length of the message
            // syscall              ; make the system call
            // mov rax, 0x2000001   ; syscall for exit
            // xor rdi, rdi         ; status code 0
            // syscall              ; make the system call
            
            ByteBuffer codeSection = ByteBuffer.allocate(1024);
            codeSection.order(ByteOrder.LITTLE_ENDIAN);
            
            // mov rax, 0x2000004 (write syscall)
            codeSection.put((byte) 0x48); // opcode for mov
            codeSection.put((byte) 0xc7);
            codeSection.put((byte) 0xc0);
            codeSection.putInt(0x2000004);
            
            // mov rdi, 1 (stdout)
            codeSection.put((byte) 0xbf);
            codeSection.putInt(1);
            
            // lea rsi, [rip+msg]
            codeSection.put((byte) 0x48);
            codeSection.put((byte) 0x8d);
            codeSection.put((byte) 0x35);
            codeSection.putInt(0); // 地址稍后更新
            
            // mov rdx, 13 (message length)
            codeSection.put((byte) 0xba);
            codeSection.putInt(13);
            
            // syscall
            codeSection.put((byte) 0x0f);
            codeSection.put((byte) 0x05);
            
            // mov rax, 0x2000001 (exit syscall)
            codeSection.put((byte) 0x48);
            codeSection.put((byte) 0xc7);
            codeSection.put((byte) 0xc0);
            codeSection.putInt(0x2000001);
            
            // xor rdi, rdi (exit code 0)
            codeSection.put((byte) 0x48);
            codeSection.put((byte) 0x31);
            codeSection.put((byte) 0xff);
            
            // syscall
            codeSection.put((byte) 0x0f);
            codeSection.put((byte) 0x05);
            
            fos.write(codeSection.array());
            
            // 写入字符串 "Hello, World!\n" 到数据段
            String helloWorld = "Hello, World!\n";
            fos.write(helloWorld.getBytes());

            System.out.println("Mach-O file with 'Hello, World!' created at " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
