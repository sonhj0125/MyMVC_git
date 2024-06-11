package myshop.controller;

import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.util.Map;

import common.controller.AbstractController;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import myshop.model.ProductDAO;
import myshop.model.ProductDAO_imple;

public class FileDownload extends AbstractController {

	private ProductDAO pdao = null;
	   
	public FileDownload() {
		pdao = new ProductDAO_imple();
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String pnum = request.getParameter("pnum");
		
		// **** 시스템에 업로드 되어진 파일설명서 첨부파일명 및 오리지널파일명 알아오기 **** //
		Map<String, String> map = pdao.getPrdmanualFileName(pnum);
		
		
		// 다운로드 할 파일의 경로를 구하고 File 객체를 생성한다.
		HttpSession session = request.getSession();
		
		ServletContext svlCtx = session.getServletContext();
		String uploadFileDir = svlCtx.getRealPath("/images");
//		System.out.println("=== 첨부되는 이미지 파일이 올라가는 절대 경로 uploadFileDir ==> " + uploadFileDir);
		// === 첨부되는 이미지 파일이 올라가는 절대 경로 uploadFileDir
		// ==> C:\NCS\workspace_jsp\.metadata\.plugins\org.eclipse.wst.server.core\tmp0\wtpwebapps\MyMVC\images
		//     개발 경로가 아닌 운영 경로로서, 이클립스에서 톰캣 서버를 지웠다 다시 추가하면 [제품 등록] 시 추가했던 이미지들이 삭제된다.
		//	   따라서 톰캣 서버를 지워야 할 일이 있다면 운영 경로에 있는 이미지들을 백업해두어야 한다. ★
		
		
		// File 객체 생성하기
		String pathname = uploadFileDir + File.separator + map.get("PRDMANUAL_SYSTEMFILENAME");
		// map.get("PRDMANUAL_SYSTEMFILENAME") 은 파일서버에 업로드 되어진 제품설명서 파일명임. 
		
		File file = new File(pathname);
		// MIME TYPE 설정하기 
        // (구글에서 검색어로 MIME TYPE 을 해보면 MIME TYPE에 따른 문서종류가 쭉 나온다)
        String mimeType = svlCtx.getMimeType(pathname);
        
     //  System.out.println("~~~~ 확인용 mimeType => " + mimeType);
     //  ~~~~ 확인용 mimeType => application/pdf  .pdf 파일임
     //  ~~~~ 확인용 mimeType => image/jpeg       .jpg 파일임
     //  ~~~~ 확인용 mimeType => application/vnd.openxmlformats-officedocument.spreadsheetml.sheet  엑셀파일임.
		
		if(mimeType == null) {
			mimeType = "application/octet-stream"; 
            // "application/octet-stream" 은 일반적으로 잘 알려지지 않은 모든 종류의 이진 데이터를 뜻하는 것임.
		}
		
		response.setContentType(mimeType);	// 다운받아야할 파일의 종류를 넣어준다.
		
		// 다운로드 되어질 파일명 알아와서 설정해주기
		String prdmanual_orginfilename = map.get("PRDMANUAL_ORGINFILENAME");	
		// map.get("PRDMANUAL_ORGINFILENAME")은 웹클라이언트의 웹브라우저에서 파일을 업로드 할때 올린 제품설명서 파일명임.
		
		// prdmanual_orginfilename(다운로드 되어지는 파일명)이 한글일때  
        // 한글 파일명이 깨지지 않도록 하기위한 웹브라우저 별로 encoding 하기 및 다운로드 파일명 설정해주기
        String downloadFileName = "";
        String header = request.getHeader("User-Agent");
        
        if (header.contains("Edge")){
           downloadFileName = URLEncoder.encode(prdmanual_orginfilename, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
         } else if (header.contains("MSIE") || header.contains("Trident")) { // IE 11버전부터는 Trident로 변경됨.
            downloadFileName = URLEncoder.encode(prdmanual_orginfilename, "UTF-8").replaceAll("\\+", "%20");
            response.setHeader("Content-Disposition", "attachment;filename=" + downloadFileName);
        } else if (header.contains("Chrome")) {
           downloadFileName = new String(prdmanual_orginfilename.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        } else if (header.contains("Opera")) {
           downloadFileName = new String(prdmanual_orginfilename.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        } else if (header.contains("Firefox")) {
           downloadFileName = new String(prdmanual_orginfilename.getBytes("UTF-8"), "ISO-8859-1");
            response.setHeader("Content-Disposition", "attachment; filename=" + downloadFileName);
        }
        
        
        // *** 다운로드할 요청 파일을 읽어서 클라이언트로 파일을 전송하기 *** //
        FileInputStream finStream = new FileInputStream(file);	// WAS 에서 읽어옴
        // 1 byte 기반 파일 입력 노드스트림 생성 --> 1 byte 는 속도가 떨어짐 --> 한꺼번에 불러와야함 new byte
		
        ServletOutputStream srvOutStream = response.getOutputStream();	// 읽어온 것을 사용
        // 1byte 기반 파일 출력 노드스트림 생성 
        // ServletOutputStream 은 바이너리 데이터를 웹 브라우저로 전송할 때 사용함.
        
        byte arrb[] = new byte[4096];	// 한꺼번에 불러오기 4kB
        int data_length = 0;
        while( (data_length = finStream.read(arrb, 0, arrb.length)) != -1) {	// data : 읽어들인 길이, 더이상 읽어들일 것이 없으면 -1
        	srvOutStream.write(arrb, 0, data_length);
        	
        } // end of while( (data_length = finStream.read(arrb, 0, arrb.length)) != -1)
        
        srvOutStream.flush();
        
    
        srvOutStream.close(); 	// 자원반납
        finStream.close();		// 자원반납
        
        
	} // end of public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception

}
