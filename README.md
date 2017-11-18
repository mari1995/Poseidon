# Poseidon

在自己学完servlet时封装的小框架，为了使用更方便。  
主要是为了解决表单提交自动注入，和查询数据时自动注入。提高代码的封装性，做到实现小项目的便捷性。  
其中使用了c3p0数据库连接池

但，深知实力有限，会继续努力！

## 主要构成

### 1）BASEAction
    servlet的父类，负责调用各个servlet中的方法，并将表单元素封装到各个实体类中

### 2）BaseDao
    针对分页的封装

### 3）DBSQL
    需要配合自定义注解来使用（ AField , Entity ）
    对 jdbc 的深层封装，实现在对数据库进行操作的时候，可以采取链式编程 

### 4）EncoderFilter
    编码过滤器，对 GET 和 POST 请求的封装，使用动态代理模式实现    

### 5）JDBCUtil
    对 jdbc 的封装，实现事务的控制    
    采用 ThreadLocal ，可以对多函数实现事务管理

### 6）JSONUtil
    对 实体类对象，对象数组，数组对象 进行封装，实现对象转化成 json 字符串

### 7）MailUtil
    对 javax.mail 包下的封装实现，实现邮件的发送，支持附件发送

### 8）typeHandler
    类型转化器，在表单提交时，对实体类类型的自动转化。和在数据查询时的类型自动转化，并封装入实体类对象中。

## 基本使用（举个栗子）
### 实体类
采用 @Entity 进行注解 ,@AField 作用在在数据库字段和实体类属性不一致的情况下，可使用该注解
如数据库字段为name, 实体类中为uname
```
    @Entity
    public class User {
        private Integer id;
        private String username;
        private String password;
        @AField("name")
        private String uname;
        private Integer sex;
        private String phone;
        private String email;
        private Date birthday;
        private Integer role;
    }
```
 
### dao 层的使用

```
   public User Login(User user) throws SQLException {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT ");
        sql.append(" id ");
        sql.append(" ,username ");
        sql.append(" ,password ");
        sql.append(" ,uname ");
        sql.append(" ,sex ");
        sql.append(" ,phone ");
        sql.append(" ,email ");
        sql.append(" ,birthday ");
        sql.append(" ,role ");
        sql.append(" From ");
        sql.append(" user ");
        sql.append(" WHERE role = ?  ");
        sql.append(" AND password = MD5(?)  ");
        sql.append(" AND ( username = ? ");
        sql.append(" OR phone = ? ");
        sql.append(" OR email = ? )");
        System.out.println(user);

        User u = (User) new DBSQL<User>(sql)
                    .addParams(0, user.getPassword(),user.getUsername(),user.getPhone(),user.getEmail())
                    .addEntity(user.getClass())
                    .queryOne();
        return u;
    }
```

### controller 层的使用 
自定义函数，形参列表中支持 HttpServletRequest , HttpServletResponse , Session ，实体类对象（需要配合注解Entity进行使用）的自动注入  
在返回字符串中，如返回 redirect:/path/file 则为重定向跳转，如返回为 /path/file 则为转发
```
    // a ) 用户登录 ( PCuserEntity )
    protected String login(HttpServletRequest request, HttpServletResponse response,Session session){
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        User PCuserEntity = userService.Login(username , passowrd);
     
        session.setAttribute("PCuserEntity",PCuserEntity);

        if ( PCuserEntity != null ) {
            return "redirect:/pc/home.jsp";
        } else {
            return "/pc/login.jsp";
        }
    }
```

### View 层使用
如果想在实现在 controller 层的自定义函数中自动注入，需要在表单中的 name 设置为实体类中的属性名也可以是注解 AField 中的name
```
<form action="" method="post">
	<div class="pc-sign">
		<input type="text" placeholder="用户名/邮箱/手机号" name="username">
	</div>
	<div class="pc-sign">
		<input type="password" placeholder="请输入您的密码" name="password">
	</div>
</form>
```
