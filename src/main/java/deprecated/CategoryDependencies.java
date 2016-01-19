/*
 * 
 * 
 */
package deprecated;

import entities.Categories;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.hibernate.Session;
import utils.HibernateUtil;
import utils.HibernateUtil;

/**
 *
 * @author Igor Klekotnev
 */
public class CategoryDependencies {
    private Integer categoryId;
    
    public CategoryDependencies(Integer categoryId) {
        this.categoryId = categoryId;
    }
    public Integer getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }   
    public ArrayList<Integer> getParentsList() {
        ArrayList<Integer> list = new ArrayList<>();
        Integer listItem = categoryId;       
        if ((listItem != 0)) {
            while ((listItem != 0)) {
                listItem = getParent(listItem);
                list.add(listItem);
            }
        }
        return list;
    }
    public ArrayList<Integer> getChildrenList() {
        ArrayList<Integer> children = new ArrayList<>();
        Integer parent = categoryId;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Categories where parent=" + parent).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Categories cat = (Categories) iterator.next();
            children.add(cat.getId());
        }
        session.close();
        return children;
    }    
    private Integer getParent(Integer id) {
        Integer parent = 0;
        Session session = HibernateUtil.getSessionFactory().openSession();
        List res = session.createQuery("From Categories where id=" + id).list();
        for (Iterator iterator = res.iterator(); iterator.hasNext();) {
            Categories cat = (Categories) iterator.next();
            parent = cat.getParent();
        }
        session.close();
        return parent;
    }    
}
