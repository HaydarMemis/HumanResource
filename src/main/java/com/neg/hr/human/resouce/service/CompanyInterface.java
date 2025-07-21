package com.neg.hr.human.resouce.service;

import com.neg.hr.human.resouce.entity.Company;

import java.util.List;
public interface CompanyInterface {

    public Company save(Company company);

    public Company findById(Long id);

    public List<Company> findAll();

    public void delete(Long id);

    public Company update(Long id, Company company);
}
