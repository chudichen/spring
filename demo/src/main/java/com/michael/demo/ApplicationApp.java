package com.michael.demo;

import com.michael.context.ApplicationContext;
import com.michael.context.support.ClassPathXmlApplicationContext;

/**
 * @author Michael Chu
 * @since 2019-08-26 19:25
 */
public class ApplicationApp {

    public static void main(String[] args) {
        ApplicationContext context = new ClassPathXmlApplicationContext("beanFactory.xml");
        MyTestBean bean = (MyTestBean) context.getBean("myTestBean");
        bean.setName("test");
        System.out.println(bean.getName());

        //{
        //    "_id" : ObjectId("5d63c4e187b8e642acb3545f"),
        //    "fazzacoCommentAnonymous" : false,
        //    "fazzacoCommentUser" : DBRef("mongo_fazzaco_user", ObjectId("5d2550ca87b8e66b6cc46bb9")),
        //    "fazzacoCommentAboutId" : "904",
        //    "fazzacoCommentRootId" : "ROOT_COMMENT",
        //    "fazzacoCommentTraderId" : NumberLong(904),
        //    "fazzacoCommentType" : NumberInt(1),
        //    "fazzacoCommentComment" : "1-1.111111111111111111111",
        //    "fazzacoCommentCommentIp" : "183.14.30.13",
        //    "fazzacoCommentIpArea" : "广东省深圳市",
        //    "fazzacoCommentImageList" : [
        //
        //    ],
        //    "fazzacoCommentScore" : DBRef("mongo_fazzaco_score", ObjectId("5d63c4e187b8e642acb3545e")),
        //    "fazzacoCommentRank" : {
        //        "fazzacoCommentLikeCount" : NumberLong(0),
        //        "fazzacoCommentReplyCount" : NumberLong(0),
        //        "fazzacoCommentRankScore" : NumberLong(0)
        //    },
        //    "fazzacoCommentList" : [
        //
        //    ],
        //    "fazzacoCommentStatus" : NumberInt(1),
        //    "fazzacoCommentCreateTime" : NumberLong(1566819553038),
        //    "fazzacoCommentUpdateTime" : NumberLong(1566819553038),
        //    "_class" : "com.fx110.fazzaco.mongo.entity.CommentMongo"
        //}
    }
}
