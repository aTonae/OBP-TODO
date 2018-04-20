package mauth.oblabs.com.firebaseauthentication.pojo;

import java.io.Serializable;
import java.util.List;

/**
 * Created by beyondthebox on 02/02/17.
 */

public class ContactData implements Serializable {


    String mobile , name ;

    String contactId;


    public ContactData(String mobile, String name, String contactId) {
        this.mobile = mobile;
        this.name = name;
        this.contactId = contactId;
    }

    public ContactData(String name, String mobile ) {

        this.name = name;
        this.mobile = mobile;
    }


    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getMobile() {
        return mobile;
    }

    public String getName() {
        return name;
    }



    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ContactData() {

    }
}
