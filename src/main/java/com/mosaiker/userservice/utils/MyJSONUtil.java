package com.mosaiker.userservice.utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

public class MyJSONUtil {
  /**
   * @function 比较两个JSONObject的key-value对的个数,内容是否一致---忽略顺序
   * @param json1
   * @param json2
   * @return
   */
  public static boolean compareTwoJSONObject(JSONObject json1,JSONObject json2) {
    if (json1==null&&json2==null) {
      return true;
    }else if (json1==null && json2!=null) {
      return false;
    }else if(json1!=null&&json2==null) {
      return false;
    }else if (json1.size()!=json2.size()) {
      int size1 = json1.size();
      int size2=json2.size();
      return false;
    }
    for(String key:json1.keySet()) {
      if ("poiid".equals(key)) {
        System.err.println();
      }
      if(!json2.containsKey(key)) {
        return false;
      }else if(json1.get(key)==null&&json2.get(key)!=null) {
        return false;
      }
      try {
        JSONObject sonJSON1 = json1.getJSONObject(key);
        JSONObject sonJSON2 = json2.getJSONObject(key);
        if(!compareTwoJSONObject(sonJSON1, sonJSON2)) {
          return false;
        }
      }catch (Exception e) {
        try {
          JSONArray sonArray1 = json1.getJSONArray(key);
          JSONArray sonArray2 = json2.getJSONArray(key);
          if(!compareTwoJSONArray(sonArray1, sonArray2)) {
            return false;
          }
        } catch (Exception e1) {
          try {
            Object o1=json1.get(key);
            Object o2=json2.get(key);
            if (!o1.equals(o2)) {
              return false;
            }
          } catch (Exception e2) {
            return false;
          }
        }
      }
    }

    return true;
  }

  /**
   * @function 比较两个JSONArray元素个数,内容是否一致---忽略顺序
   * @param array1
   * @param array2
   * @return	一致返回true,不一致返回false
   */
  public static boolean compareTwoJSONArray(JSONArray array1,JSONArray array2) {
    if (array1==null&&array2==null) {
      return true;
    }else if (array1==null && array2!=null) {
      return false;
    }else if(array1!=null&&array2==null) {
      return false;
    }else if (array1.size()!=array2.size()) {
      return false;
    }
    for(int index=0;index<array1.size();index++) {
      //array1的第index个元素还是JSONArray,则遍历array2的所有元素,递归比较...
      try {
        JSONArray sonArray1 = array1.getJSONArray(index);
        boolean flag = false;
        for(int index2=0;index2<array2.size();index2++) {
          JSONArray sonArray2 = array2.getJSONArray(index2);
          if(compareTwoJSONArray(sonArray1,sonArray2)) {
            flag = true;
          }
        }
        if (!flag) {
          return false;
        }
      } catch (Exception e) {
        //array1的第index个元素是JSONObject,则遍历array2的所有元素,递归比较...
        try {
          JSONObject sonJSON1 = array1.getJSONObject(index);
          boolean flag = false;
          for(int index2=0;index2<array2.size();index2++) {
            JSONObject sonJSON2 = array2.getJSONObject(index2);
            if(compareTwoJSONObject(sonJSON1,sonJSON2)) {
              flag = true;
            }
          }
          if (!flag) {
            return false;
          }
        } catch (Exception e1) {
          //array1的第index个元素非JSONArray&&非JSONObject,则遍历array2的所有元素,递归比较...
          try {
            Object o1 = array1.get(index);
            boolean flag = false;
            for(int index2=0;index2<array2.size();index2++) {
              Object o2 = array2.get(index2);
              if((o1==null&&o2==null)||o1.equals(o2)) {
                flag = true;
              }
            }
            if (!flag) {
              return false;
            }
          }catch (Exception e2) {
            array1.equals(array2);
            return false;
          }
        }
      }
    }
    return true;
  }
  
}
