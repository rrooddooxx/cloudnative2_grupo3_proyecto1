package com.cn2g3.bff.config;

public class BffConstants {
  public static final String FN1_GET_PRODUCTS = "/api/get-products?warehouses={show}";
  public static final String FN1_GET_PRODUCTS_ACTION_SHOW = "show";
  public static final String FN1_GET_PRODUCTS_ACTION_HIDE = "hide";
  public static final String FN1_DELETE_WAREHOUSE = "/api/delete-warehouse/{id}";
  public static final String FN2_ADD_ACTION = "add";
  public static final String FN2_UPDATE_ACTION_QUERY = "action";
  public static final String FN2_UPDATE_ACTION = "update";
  public static final String FN2_UPDATE_PATH = "/api/update-product?action={action}";
  public static final String FN3_DELETE_PATH = "/api/delete-product/{id}";
  public static final String GQL_PATH = "/api/graphql";

  private BffConstants() {}
}
