package model.dao.impl;

import model.dao.DepartmentDao;
import model.entities.Department;
import db.DB;
import db.DbException;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoJDBC implements DepartmentDao {

    private Connection connection;

    public DepartmentDaoJDBC(Connection conn){
        connection = conn;
    }

    @Override
    public void insert(Department obj) {
        PreparedStatement st = null;
        ResultSet sr = null;

        try{
            st = connection.prepareStatement("INSERT INTO department VALUES (null, ?)", PreparedStatement.RETURN_GENERATED_KEYS);
            st.setString(1, obj.getName());
            st.executeUpdate();
            sr = st.getGeneratedKeys();

            if(sr.next()){
                System.out.println("Was insert data with id: " + sr.getInt(1));
            }
            else{
                throw new DbException("Unexpected error! No rows affected!");
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(sr);
        }

    }

    @Override
    public void update(Department obj) {
        PreparedStatement st = null;

        try{
            st = connection.prepareStatement("UPDATE department SET Name = ? WHERE Id = ?");
            st.setString(1, obj.getName());
            st.setInt(2, obj.getId());
            int right = st.executeUpdate();

            if(right != 0){
                System.out.println("Was update department with suceffully");
            }
            else{
                throw new DbException("Unexpected error! No rows affected!");
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public void deleteById(Integer id) {
        PreparedStatement st = null;

        try{
            st = connection.prepareStatement("DELETE FROM department WHERE Id = ?");
            st.setInt(1, id);
            st.executeUpdate();


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
        }
    }

    @Override
    public Department findById(Integer id) {
        PreparedStatement st = null;
        ResultSet sr = null;

        try{
            st = connection.prepareStatement("SELECT Id, Name FROM department WHERE Id = ?");
            st.setInt(1, id);
            sr = st.executeQuery();


            if(sr.next()){
                Department department = convertDepartment(sr);
                return department;
            }
            else{
                throw new DbException("there is not department with Id " + id);
            }


        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(sr);
        }
    }

    @Override
    public List<Department> findAll() {
        PreparedStatement st = null;
        ResultSet sr = null;

        try{
            st = connection.prepareStatement("SELECT Id, Name FROM department");
            sr = st.executeQuery();
            List<Department> allDepartments = new ArrayList<>();

            while(sr.next()){
                Department department = convertDepartment(sr);
                allDepartments.add(department);
            }

            return allDepartments;

        } catch (SQLException e) {
            throw new DbException(e.getMessage());
        }
        finally {
            DB.closeStatement(st);
            DB.closeResultSet(sr);
        }
    }

    private static Department convertDepartment(ResultSet rs){
        Department department = new Department();
        try {
            department.setId(rs.getInt(1));
            department.setName(rs.getString(2));
            return department;

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
