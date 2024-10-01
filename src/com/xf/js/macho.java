package com.xf.js;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class macho {

    public static void main(String[] args) {
        String outputFile = "out/hello";
        segment_command_64 s = new segment_command_64("TEXT", LC_SEGMENT_64);
        section_64 sec = new section_64("_text","_TEXT");
        section_64 sec2 = new section_64("__unwind_info","_TEXT");
        s.appendSection(sec);
        s.appendSection(sec2);
        s.toArray();
        if(1>0)
        return;

        try (FileOutputStream fos = new FileOutputStream(outputFile)) {
            byte[] text_text = makeLCSegment();
            byte lc_segment_64[] = makeSegment();
            
            byte[] mainCommand = makeLCMainCommand();
            byte[] code = makeCode();
            
            byte[] dataSeg = "Hello, World!\n".getBytes();

            byte[] machoHeader = makeMachoHeader();
            
            fos.write(machoHeader);
            fos.write(lc_segment_64);
            //fos.write(text_text);
            fos.write(mainCommand);

            fos.write(code);
            fos.write(dataSeg);            // 写入字符串 "Hello, World!\n" 到数据段

            System.out.println("Mach-O file with 'Hello, World!' created at " + outputFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private static byte[] makeCode() {
        int fileSize = 1024;
        // 写入代码段，执行系统调用，输出 "Hello, World!"
        ByteBuffer codeSection = ByteBuffer.allocate((int) fileSize);
        codeSection.order(ByteOrder.LITTLE_ENDIAN);
        
        // mov rax, 0x2000004 (write syscall)
//        codeSection.put((byte) 0x48); codeSection.put((byte) 0xc7);
//        codeSection.put((byte) 0xc0); codeSection.putInt(0x2000004);
//
//        // mov rdi, 1 (stdout)
//        codeSection.put((byte) 0xbf); codeSection.putInt(1);
//
//        // lea rsi, [rip+msg] (load address of "Hello, World!" message)
//        codeSection.put((byte) 0x48); codeSection.put((byte) 0x8d);
//        codeSection.put((byte) 0x35); codeSection.putInt(12); // Adjust the offset
//        
//        // mov rdx, 13 (message length)
//        codeSection.put((byte) 0xba); codeSection.putInt(13);
//
//        // syscall
        byte[] syscall = {0x0f, 0x05};
        //codeSection.put(syscall);

        // mov rax, 0x2000001 (exit syscall)
        codeSection.put((byte) 0x48); codeSection.put((byte) 0xc7);
        codeSection.put((byte) 0xc0); codeSection.putInt(0x2000001);

        // xor rdi, rdi (exit code 0)
        byte[] xor_rdi_rdi = {0x48, 0x31, (byte)0xff};
		codeSection.put(xor_rdi_rdi);
		
        byte[] mov_ebx_0 = {(byte)0xbb, 0x00, 0x00, 0x00, 0x00};		// movl	$0, %ebx
        byte[] mov_eax_200001 = {(byte)0xb8, 0x01, 0x00, 0x00, 0x02}; 	// movl	$0x2000001, %eax         ## imm = 0x2000001
        codeSection.put(mov_ebx_0);
        codeSection.put(mov_eax_200001);
        // syscall
        codeSection.put(syscall);
        byte[] code = codeSection.array();
		return code;
	}

	private static byte[] makeLCSegment() {
		segment_command_64 s = new segment_command_64("__TEXT", LC_SEGMENT_64);
        s.cmdsize = 56 + 8+8;
        s.vmaddr = 0x0100000000L;	// vmaddr (virtual memory address placeholder)
        s.vmsize = 0x1000; 			// size for small code
        s.fileoff = 0;	//fileoff (file offset)//0x100
        // filesize (file size)
        long codeSize = 1024;  // 假设代码段的大小为 1024 字节
        //long fileSize = 4096; //1024 + 13;  // 假设代码段为 1024 字节，字符串为 13 字节
        s.filesize = 32 + 72 + 24 + codeSize;  // 文件头 + 段命令 + LC_MAIN + 代码段

        s.maxprot =  VM_PROT_READ | VM_PROT_EXECUTE; 
        s.initprot = VM_PROT_READ | VM_PROT_EXECUTE;
        s.nsects = 0;
        s.flags = 0;
        
        byte[] dat = s.toArray();
		return dat;
	}
    
	private static byte[] makeLCMainCommand() {
        // Load command LC_MAIN
        ByteBuffer mainCommand = ByteBuffer.allocate(24);
        mainCommand.order(ByteOrder.LITTLE_ENDIAN);
        
        mainCommand.putInt(LC_MAIN);  // cmd, LC_MAIN = 0x80000028
        mainCommand.putInt(24);          // cmdsize

        long entryOffset = 32 + 72 + 24;
        //entryOffset = 0x100; 
        mainCommand.putLong(entryOffset);  // // Entry point offset from the beginning of the Mach-O file, Adjust for actual entry point offset
        // Stack size
        mainCommand.putLong(0);      // Stack size (0 for default)

        byte[] data = mainCommand.array();
		return data;
	}
	private static byte[] makeMachoHeader() {
        int MH_NOUNDEFS = 0x00000001;        		// Flags (e.g., MH_NOUNDEFS = 0x00000001)

        int magic = 0xfeedfacf;       		 		// Magic number for 64-bit Mach-O (0xfeedfacf)
    	int CPU_TYPE_X86_64 = 0x01000007;	        // CPU type (CPU_TYPE_X86_64 = 0x01000007)
        int CPU_SUBTYPE_X86_64_ALL = 0x00000003;	// CPU subtype (CPU_SUBTYPE_X86_64_ALL = 0x00000003)
        int MH_EXECUTE = 0x00000002;				// File type (MH_EXECUTE = 0x00000002, executable file)
        int numCommands = 2;						// Number of load commands        // LC_SEGMENT_64 + LC_MAIN
        int sizeOfLoadCommands = 56+16 + 24; 		// 56 bytes for LC_SEGMENT_64, 24 bytes for LC_MAIN
        int flags = MH_NOUNDEFS;
    	
        ByteBuffer header = ByteBuffer.allocate(32);
        header.order(ByteOrder.LITTLE_ENDIAN);
        
        header.putInt(magic);
        header.putInt(CPU_TYPE_X86_64);
        header.putInt(CPU_SUBTYPE_X86_64_ALL);
        header.putInt(MH_EXECUTE);
        header.putInt(numCommands);
        header.putInt(sizeOfLoadCommands);
        header.putInt(flags);
        byte[] code = header.array();
        return code;
    }
	private static byte[] makeSegment() {
        // Load command LC_SEGMENT_64 for __TEXT segment
        int cmdsize = 56 + 8+8;
        ByteBuffer segmentCommand = ByteBuffer.allocate(cmdsize);
        segmentCommand.order(ByteOrder.LITTLE_ENDIAN);

        // LC_SEGMENT_64 = 0x19
        segmentCommand.putInt(0x19);  // cmd
        segmentCommand.putInt(cmdsize);    // cmdsize
        segmentCommand.put("__TEXT".getBytes()); // segment name __TEXT, 16bytes
        segmentCommand.position(24);  // Skip unused segment name bytes
        // vmaddr (virtual memory address placeholder)
        segmentCommand.putLong(0x0100000000L);
        // vmsize (virtual memory size placeholder)
        segmentCommand.putLong(0x1000); // size for small code
        // fileoff (file offset)
        segmentCommand.putLong(0);	//0x100
        // filesize (file size)
        long codeSize = 1024;  // 假设代码段的大小为 1024 字节
        //long fileSize = 4096; //1024 + 13;  // 假设代码段为 1024 字节，字符串为 13 字节
        long fileSize = 32 + 72 + 24 + codeSize;  // 文件头 + 段命令 + LC_MAIN + 代码段

        segmentCommand.putLong(fileSize);  // filesize
        
//        segmentCommand.putLong(codeSize); // size for small code
        // maxprot (read, write, execute)
        segmentCommand.putInt(5); // VM_PROT_READ | VM_PROT_WRITE | VM_PROT_EXECUTE
        // initprot (read, execute)
        segmentCommand.putInt(5); // VM_PROT_READ | VM_PROT_EXECUTE
        // Number of sections
        segmentCommand.putInt(0);
        // Flags
        segmentCommand.putInt(0);		
		return segmentCommand.array();
	}
	static int VM_PROT_READ			= 1;
	static int VM_PROT_WRITE		= 2;
	static int VM_PROT_EXECUTE		= 4;
	
	static int LC_SYMTAB			= 0x02;
	static int LC_LOAD_DYLINKER		= 0x07;
	static int LC_DYSYMTAB			= 0x0b;
	static int LC_LOAD_DYLIB		= 0x0c;
	static int LC_SEGMENT_64 		= 0x19;
	static int LC_DYLD_INFO_ONLY	= 0x22;
	static int LC_MAIN 				= 0x80000028;
	
	static class base{
        byte[] toArray() {
        	byte[]bytes = null;
        	try {
	        	Field[] fields = super.getClass().getDeclaredFields();
	//        	fields = this.getClass().getSuperclass().getDeclaredFields();
                ByteBuffer buf = ByteBuffer.allocate(8192);
                buf.order(ByteOrder.LITTLE_ENDIAN);
	        	for(Field f:fields) {
	        		Class<?> type = f.getType();
//	        		System.out.println(f.toString());
	        		if(type == int.class) {
	                    buf.putInt(f.getInt(this));         			
	        		}
	        		else if(type == long.class) {
	                    buf.putLong(f.getLong(this));         			
	        		}
	        		else if(type == String.class) {
	        			byte[] name = new byte[16];
	        			String s = (String)f.get(this);
	        			int len = (s.length() > name.length)?name.length:s.length();
	        			System.arraycopy(s.getBytes(), 0, name, 0, len);
	                    buf.put(name); 
//	                    buf.position(24);
	        		}
	        	}
	        	bytes = buf.array();
        	}
        	catch(Exception e) {
        		System.out.println(e.toString());
        	}
        	return bytes;
        }
	}
	
	static class segment_command_64 extends base{ /* for 64-bit architectures */
		int cmd;        /* LC_SEGMENT_64 */
		int cmdsize;    /* includes sizeof section_64 structs */
		String segname;	/* segment name */ //16 bytes
        long vmaddr; 	/* memory address of this segment */
        long vmsize; 	/* memory size of this segment */
        long fileoff;	/* file offset of this segment */
        long filesize;	/* amount to map from the file */
        int maxprot;	/* maximum VM protection */// maxprot (read, write, execute) // VM_PROT_READ | VM_PROT_WRITE | VM_PROT_EXECUTE
        int initprot;	/* initial VM protection */// initprot (read, execute) // VM_PROT_READ | VM_PROT_EXECUTE
        int nsects;		/* number of sections in segment */// Number of sections
        int flags;		/* flags */
        section_64[] sections;
        
        public segment_command_64(String _segname, int _cmd) {
            segname = _segname;
            cmd = _cmd;
            cmdsize = 72; // 0 section 
		}
        public void appendSection(section_64 sec) {
        	int new_num = nsects + 1;
        	section_64[] ns = new section_64[new_num];
        	for(int i=0;i<nsects;i++)
            	ns[i] = sections[i];
        	ns[nsects] = sec;
        	nsects = new_num;
        	sections = ns;
        	cmdsize += 80;	// add size of section
        }

//		byte[] toArray() {
//        	ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        	java.io.DataOutputStream dos = new java.io.DataOutputStream(baos);
//
//        	cmdsize = 72;
//            ByteBuffer buf = ByteBuffer.allocate(8192);
//            buf.order(ByteOrder.LITTLE_ENDIAN);
//
//            buf.putInt(cmd); 
//            buf.putInt(cmdsize);
//            buf.put(segname.getBytes()); 
//            buf.position(24);  // Skip unused segment name bytes
//            buf.putLong(vmaddr);
//            buf.putLong(vmsize); // size for small code
//            buf.putLong(fileoff);	//0x100
//            // filesize (file size)
////            long codeSize = 1024;  // 假设代码段的大小为 1024 字节
//            //long fileSize = 4096; //1024 + 13;  // 假设代码段为 1024 字节，字符串为 13 字节
////            long fileSize = 32 + 72 + 24 + codeSize;  // 文件头 + 段命令 + LC_MAIN + 代码段
//
//            buf.putLong(fileSize);  // filesize
//            buf.putInt(maxprot);
//            buf.putInt(initprot);
//            buf.putInt(nsects);
//            buf.putInt(flags);		
//        	
//            byte[] data = new byte[cmdsize];
//            buf.flip();
//            buf.get(data);
//        	return data;
//        }
	}

	static class symtab_command extends base{
		int cmd; 		/* LC_SYMTAB */
		int cmdsize;	/* sizeof(class symtab_command) */
        int symoff;		/* symbol table offset */
        int nsyms;		/* number of symbol table entries */
        int stroff;		/* string table offset */
        int strsize;	/* string table size in bytes */
        
        public symtab_command() {
            this.cmd = LC_SYMTAB;
            cmdsize = 24;
		}
	}
	
	static class section_64 extends base{ /* for 64-bit architectures */
//		byte[]		sectname = new byte[16];	/* name of this section */
//		byte[]		segname = new byte[16];	/* segment this section goes in */
		String		sectname;	/* name of this section */
		String		segname;	/* segment this section goes in */
		long	addr;		/* memory address of this section */
		long	size;		/* size in bytes of this section */
		int	offset;		/* file offset of this section */
		int	align;		/* section alignment (power of 2) */
		int	reloff;		/* file offset of relocation entries */
		int	nreloc;		/* number of relocation entries */
		int	flags;		/* flags (section type and attributes)*/
		int	reserved1;	/* reserved (for offset or index) */
		int	reserved2;	/* reserved (for count or sizeof) */
		int	reserved3;	/* reserved */
		
		section_64(String _sectname, String _segname) {
			sectname = _sectname;
			segname = _segname;
		}
	}

	
	class dysymtab_command extends base{
	    int cmd;		/* LC_DYSYMTAB */
	    int cmdsize;	/* sizeof(class dysymtab_command) */
	    int ilocalsym;	/* index to local symbols */
	    int nlocalsym;	/* number of local symbols */
	    int iextdefsym;	/* index to externally defined symbols */
	    int nextdefsym;	/* number of externally defined symbols */
	    int iundefsym;	/* index to undefined symbols */
	    int nundefsym;	/* number of undefined symbols */
	    int tocoff;		/* file offset to table of contents */
	    int ntoc;		/* number of entries in table of contents */
	    int modtaboff;	/* file offset to module table */
	    int nmodtab;	/* number of module table entries */
	    int extrefsymoff;	/* offset to referenced symbol table */
	    int nextrefsyms;	/* number of referenced symbol table entries */
	    int indirectsymoff; /* file offset to the indirect symbol table */
	    int nindirectsyms;  /* number of indirect symbol table entries */
	    int extreloff;	/* offset to external relocation entries */
	    int nextrel;	/* number of external relocation entries */
	    int locreloff;	/* offset to local relocation entries */
	    int nlocrel;	/* number of local relocation entries */

	    dysymtab_command() {
	    	cmd = LC_DYSYMTAB;
	    	cmdsize = 80;
	    }
	}

	static class dyld_info_command extends base{
		int   cmd;		/* LC_DYLD_INFO or LC_DYLD_INFO_ONLY */
		int   cmdsize;		/* sizeof(class dyld_info_command) */
	    int   rebase_off;	/* file offset to rebase info  */
	    int   rebase_size;	/* size of rebase info   */
	    int   bind_off;	/* file offset to binding info   */
	    int   bind_size;	/* size of binding info  */
	    int   weak_bind_off;	/* file offset to weak binding info   */
	    int   weak_bind_size;  /* size of weak binding info  */	    
	    int   lazy_bind_off;	/* file offset to lazy binding info */
	    int   lazy_bind_size;  /* size of lazy binding infs */
	    int   export_off;	/* file offset to lazy binding info */
	    int   export_size;	/* size of lazy binding infs */
	    
	    dyld_info_command() {
	    	cmd = LC_DYLD_INFO_ONLY;
	    	cmdsize = 48;
	    }
	};
//	union lc_str {
//		uint32_t	offset;	/* offset to the string */
//	#ifndef __LP64__
//		char		*ptr;	/* pointer to the string */
//	#endif 
//	};

	static class dylinker_command extends base{
		int	cmd;		/* LC_ID_DYLINKER, LC_LOAD_DYLINKER or
						   LC_DYLD_ENVIRONMENT */
		int	cmdsize;	/* includes pathname string, 16 bytes align? */
//		lc_str name;
		int name;		/* dynamic linker's path name */
		
		dylinker_command() {
			cmd = LC_LOAD_DYLINKER;
//			cmdsize = 
		}
	};

	static class dylib extends base{
		int name;			/* lc_str library's path name */
	    int timestamp;			/* library's build time stamp */
	    int current_version;		/* library's current version number */
	    int compatibility_version;	/* library's compatibility vers number*/
	    
	    dylib() {
	    	
	    }
	};

	/*
	 * A dynamically linked shared library (filetype == MH_DYLIB in the mach header)
	 * contains a dylib_command (cmd == LC_ID_DYLIB) to identify the library.
	 * An object that uses a dynamically linked shared library also contains a
	 * dylib_command (cmd == LC_LOAD_DYLIB, LC_LOAD_WEAK_DYLIB, or
	 * LC_REEXPORT_DYLIB) for each library it uses.
	 */
	static class dylib_command extends base{
		int	cmd;			/* LC_ID_DYLIB, LC_LOAD_{,WEAK_}DYLIB,
						   		LC_REEXPORT_DYLIB */
		int	cmdsize;		/* includes pathname string */
		dylib dylib;		/* the library identification */
		
		dylib_command() {
			cmd = LC_LOAD_DYLIB;
//			cmdsize = 
		}
	};
	
	static class entry_point_command extends base{
		int  cmd;			/* LC_MAIN only used in MH_EXECUTE filetypes */
		int  cmdsize;		/* 24 */
	    long  entryoff;		/* file (__TEXT) offset of main() */
	    long  stacksize;	/* if not zero, initial stack size */
	    
	    entry_point_command(long _entryoff, long _stacksize) {
	    	cmd = LC_MAIN;
	    	cmdsize = 24;
	    	entryoff = _entryoff;
			stacksize = _stacksize;
	    }
	};

}
