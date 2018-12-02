package server.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import server.integration.CatalogDAO;
import server.integration.CatalogDBException;
/**
 *
 * @author yuchen
 */
public class Catalog {
    private List<File> files = Collections.synchronizedList(new ArrayList<>());
    private final CatalogDAO catDAO;
    
    public Catalog(String dbms, String datasource) throws CatalogDBException {
        System.out.println("In catalog");
        this.catDAO = new CatalogDAO(dbms, datasource);
        System.out.println("after catdao");
    }
    
    public void addFile(File file) throws CatalogDBException{ 
        catDAO.createFile(file);
        //files.add(file);
    }
    
    public String[] getFiles() {
        String[] filelist = new String[files.size()];
        for(int i = 0; i < files.size(); i++){
            filelist[i] = files.get(i).name;
        }
        return filelist;
    }
    
    public File makeFile(String filename, int size, String owner, String permission){
        File file = new File(filename, size, owner, permission);
        return file;
    }
    
    public File getFile(String filename){
        for(int i = 0; i < files.size(); i++){
            File fileOpen = files.get(i);
            if(filename.equals(fileOpen.name)){
                return fileOpen;
            }
        }
        return null;
    }
}
