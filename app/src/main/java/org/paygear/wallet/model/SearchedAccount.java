package org.paygear.wallet.model;

import java.util.List;

public class SearchedAccount {
    /**
     * _id : 5b5043fd7c2f88000bf2fdf4
     * account_type : 0
     * is_vip : false
     * location : [35.740286064023756,51.50158345699311]
     * name : amin
     * sub_title : a
     * username :
     * profile_picture : images/2017/07/03/39edd4441012494f9df20e112db2e7e320170703_413x413.jpg
     */

    private String _id;
    private int account_type;
    private int business_type;
    private boolean is_vip;
    private String name;
    private String sub_title;
    private String username;
    private String profile_picture;
    private List<Double> location;

    public int getBusiness_type() {
        return business_type;
    }

    public void setBussiness_type(int bussines_type) {
        this.business_type = bussines_type;
    }

    public List<UsersBean> getUsers() {
        return users;
    }

    public void setUsers(List<UsersBean> users) {
        this.users = users;
    }

    private List<UsersBean> users;

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public int getAccount_type() {
        return account_type;
    }

    public void setAccount_type(int account_type) {
        this.account_type = account_type;
    }

    public boolean isIs_vip() {
        return is_vip;
    }

    public void setIs_vip(boolean is_vip) {
        this.is_vip = is_vip;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSub_title() {
        return sub_title;
    }

    public void setSub_title(String sub_title) {
        this.sub_title = sub_title;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getProfile_picture() {
        return profile_picture;
    }

    public void setProfile_picture(String profile_picture) {
        this.profile_picture = profile_picture;
    }

    public List<Double> getLocation() {
        return location;
    }

    public void setLocation(List<Double> location) {
        this.location = location;
    }
    public static class UsersBean {
            /**
             * user_id : 59abfecfb164681dd8379af6
             * role : finance
             */

            private String user_id;
            private String role;

            public String getUser_id() {
                return user_id;
            }

            public void setUser_id(String user_id) {
                this.user_id = user_id;
            }

            public String getRole() {
                return role;
            }

            public void setRole(String role) {
                this.role = role;
            }
        }


//    /**
//     * type : 1
//     * account : {"username":"unknown_8384","name":"سعید کامیابی","location":{"lat":35.7778216,"lon":51.418686},"city_id":342,"profile_picture":"images/2017/07/06/176c52fe286745c4b6402a2c9ce3542b20170706_200x200.jpg","sub_title":"جردن","account_type":0,"users":[{"user_id":"59abfecfb164681dd8379af6","role":"finance"},{"user_id":"59abfecfb164681dd8379af6","role":"admin"}],"province_id":317,"tags":["راد","رادسنس","رایانه والکترونیک","پرداخت"],"_id":"59abff37b164681dd837d110","province":{"id":317,"name":"تهران","phone_code":null,"type":1}}
//     */
//
//    private int type;
//    private AccountBean account;
//
//    public int getType() {
//        return type;
//    }
//
//    public void setType(int type) {
//        this.type = type;
//    }
//
//    public AccountBean getAccount() {
//        return account;
//    }
//
//    public void setAccount(AccountBean account) {
//        this.account = account;
//    }
//
//    public static class AccountBean {
//        /**
//         * username : unknown_8384
//         * name : سعید کامیابی
//         * location : {"lat":35.7778216,"lon":51.418686}
//         * city_id : 342
//         * profile_picture : images/2017/07/06/176c52fe286745c4b6402a2c9ce3542b20170706_200x200.jpg
//         * sub_title : جردن
//         * account_type : 0
//         * users : [{"user_id":"59abfecfb164681dd8379af6","role":"finance"},{"user_id":"59abfecfb164681dd8379af6","role":"admin"}]
//         * province_id : 317
//         * tags : ["راد","رادسنس","رایانه والکترونیک","پرداخت"]
//         * _id : 59abff37b164681dd837d110
//         * province : {"id":317,"name":"تهران","phone_code":null,"type":1}
//         */
//
//        private String username;
//        private String name;
//        private LocationBean location;
//        private int city_id;
//        private String profile_picture;
//        private String sub_title;
//        private int account_type;
//        private int province_id;
//        private String _id;
//        private ProvinceBean province;
//        private List<UsersBean> users;
//        private List<String> tags;
//
//        public String getUsername() {
//            return username;
//        }
//
//        public void setUsername(String username) {
//            this.username = username;
//        }
//
//        public String getName() {
//            return name;
//        }
//
//        public void setName(String name) {
//            this.name = name;
//        }
//
//        public LocationBean getLocation() {
//            return location;
//        }
//
//        public void setLocation(LocationBean location) {
//            this.location = location;
//        }
//
//        public int getCity_id() {
//            return city_id;
//        }
//
//        public void setCity_id(int city_id) {
//            this.city_id = city_id;
//        }
//
//        public String getProfile_picture() {
//            return profile_picture;
//        }
//
//        public void setProfile_picture(String profile_picture) {
//            this.profile_picture = profile_picture;
//        }
//
//        public String getSub_title() {
//            return sub_title;
//        }
//
//        public void setSub_title(String sub_title) {
//            this.sub_title = sub_title;
//        }
//
//        public int getAccount_type() {
//            return account_type;
//        }
//
//        public void setAccount_type(int account_type) {
//            this.account_type = account_type;
//        }
//
//        public int getProvince_id() {
//            return province_id;
//        }
//
//        public void setProvince_id(int province_id) {
//            this.province_id = province_id;
//        }
//
//        public String get_id() {
//            return _id;
//        }
//
//        public void set_id(String _id) {
//            this._id = _id;
//        }
//
//        public ProvinceBean getProvince() {
//            return province;
//        }
//
//        public void setProvince(ProvinceBean province) {
//            this.province = province;
//        }
//
//        public List<UsersBean> getUsers() {
//            return users;
//        }
//
//        public void setUsers(List<UsersBean> users) {
//            this.users = users;
//        }
//
//        public List<String> getTags() {
//            return tags;
//        }
//
//        public void setTags(List<String> tags) {
//            this.tags = tags;
//        }
//
//        public static class LocationBean {
//            /**
//             * lat : 35.7778216
//             * lon : 51.418686
//             */
//
//            private double lat;
//            private double lon;
//
//            public double getLat() {
//                return lat;
//            }
//
//            public void setLat(double lat) {
//                this.lat = lat;
//            }
//
//            public double getLon() {
//                return lon;
//            }
//
//            public void setLon(double lon) {
//                this.lon = lon;
//            }
//        }
//
//        public static class ProvinceBean {
//            /**
//             * id : 317
//             * name : تهران
//             * phone_code : null
//             * type : 1
//             */
//
//            private int id;
//            private String name;
//            private Object phone_code;
//            private int type;
//
//            public int getId() {
//                return id;
//            }
//
//            public void setId(int id) {
//                this.id = id;
//            }
//
//            public String getName() {
//                return name;
//            }
//
//            public void setName(String name) {
//                this.name = name;
//            }
//
//            public Object getPhone_code() {
//                return phone_code;
//            }
//
//            public void setPhone_code(Object phone_code) {
//                this.phone_code = phone_code;
//            }
//
//            public int getType() {
//                return type;
//            }
//
//            public void setType(int type) {
//                this.type = type;
//            }
//        }
//
//        public static class UsersBean {
//            /**
//             * user_id : 59abfecfb164681dd8379af6
//             * role : finance
//             */
//
//            private String user_id;
//            private String role;
//
//            public String getUser_id() {
//                return user_id;
//            }
//
//            public void setUser_id(String user_id) {
//                this.user_id = user_id;
//            }
//
//            public String getRole() {
//                return role;
//            }
//
//            public void setRole(String role) {
//                this.role = role;
//            }
//        }
//    }


}
