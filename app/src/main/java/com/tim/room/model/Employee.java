package com.tim.room.model;


/**
 * Created by Zeng on 2017/2/6.
 */

public class Employee {
    public Employee() {

    }

    public Employee(String empHireDate, String empName, int empSalary, RoleBean role) {
        this.empHireDate = empHireDate;
        this.empName = empName;
        this.empSalary = empSalary;
        this.role = role;
    }

    /**
     * empHireDate : 2010-10-10T08:00:00
     * empId : 1
     * empName : Tim
     * empSalary : 123
     * role : {"employees":[],"id":3,"roleName":"leader"}
     */
    private String empHireDate;
    private String empName;
    private Integer empSalary;

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    private Integer empId;
    private RoleBean role;

    public String getEmpHireDate() {
        return empHireDate;
    }

    public void setEmpHireDate(String empHireDate) {
        this.empHireDate = empHireDate;
    }


    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    public int getEmpSalary() {
        return empSalary;
    }

    public void setEmpSalary(int empSalary) {
        this.empSalary = empSalary;
    }

    public RoleBean getRole() {
        return role;
    }

    public void setRole(RoleBean role) {
        this.role = role;
    }

    public static class RoleBean {
        /**
         * employees : []
         * id : 3
         * roleName : leader
         */
        private int id;
        private String roleName;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getRoleName() {
            return roleName;
        }

        public void setRoleName(String roleName) {
            this.roleName = roleName;
        }

    }
}
