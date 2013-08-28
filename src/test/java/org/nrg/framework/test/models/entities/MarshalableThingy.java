/*
 * org.nrg.framework.test.models.entities.MarshalableThingy
 * XNAT http://www.xnat.org
 * Copyright (c) 2013, Washington University School of Medicine
 * All Rights Reserved
 *
 * Released under the Simplified BSD.
 *
 * Last modified 7/1/13 5:30 PM
 */
package org.nrg.framework.test.models.entities;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement
public class MarshalableThingy {

    public void setName(String name) {
        _name = name;
    }
    public String getName() {
        return _name;
    }
    public void setAddress(String address) {
        _address = address;
    }
    public String getAddress() {
        return _address;
    }
    public void setAge(int age) {
        _age = age;
    }
    public int getAge() {
        return _age;
    }
    public void setNotImportant(String notImportant) {
        _notImportant = notImportant;
    }
    @XmlTransient
    public String getNotImportant() {
        return _notImportant;
    }

    private String _name;
    private String _address;
    private int _age;
    private String _notImportant;
}
