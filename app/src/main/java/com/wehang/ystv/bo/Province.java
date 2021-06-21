package com.wehang.ystv.bo;

import com.whcd.base.interfaces.BaseData;

import java.util.List;

/**
 * Created by lenovo on 2017/8/10.
 */

public class Province extends BaseData {
    /**
     * id : 2
     * name : 北京市
     * children : [{"id":33,"name":"市辖区","children":[{"id":378,"name":"东城区","children":[]},{"id":379,"name":"西城区","children":[]},{"id":382,"name":"朝阳区","children":[]},{"id":383,"name":"丰台区","children":[]},{"id":384,"name":"石景山区","children":[]},{"id":385,"name":"海淀区","children":[]},{"id":386,"name":"门头沟区","children":[]},{"id":387,"name":"房山区","children":[]},{"id":388,"name":"通州区","children":[]},{"id":389,"name":"顺义区","children":[]},{"id":390,"name":"昌平区","children":[]},{"id":391,"name":"大兴区","children":[]},{"id":392,"name":"怀柔区","children":[]},{"id":393,"name":"平谷区","children":[]}]},{"id":34,"name":"县","children":[{"id":394,"name":"密云县","children":[]},{"id":395,"name":"延庆县","children":[]}]}]
     */

    public int id;
    public String name;
    public List<ChildrenBeanX> children;

    public static class ChildrenBeanX {
        /**
         * id : 33
         * name : 市辖区
         * children : [{"id":378,"name":"东城区","children":[]},{"id":379,"name":"西城区","children":[]},{"id":382,"name":"朝阳区","children":[]},{"id":383,"name":"丰台区","children":[]},{"id":384,"name":"石景山区","children":[]},{"id":385,"name":"海淀区","children":[]},{"id":386,"name":"门头沟区","children":[]},{"id":387,"name":"房山区","children":[]},{"id":388,"name":"通州区","children":[]},{"id":389,"name":"顺义区","children":[]},{"id":390,"name":"昌平区","children":[]},{"id":391,"name":"大兴区","children":[]},{"id":392,"name":"怀柔区","children":[]},{"id":393,"name":"平谷区","children":[]}]
         */

        public int id;
        public String name;
        public List<ChildrenBean> children;

        public static class ChildrenBean {
            /**
             * id : 378
             * name : 东城区
             * children : []
             */

            public int id;
            public String name;
            public List<?> children;

            @Override
            public String toString() {
                return "ChildrenBean{" +
                        "name='" + name + '\'' +
                        '}';
            }
        }
    }

    @Override
    public String toString() {
        return "Province{" +
                "name='" + name + '\'' +
                ", children=" + children.toString() +
                '}';
    }
}
