/*
* 封装 将前端传的数据以异步刷新的方法提交json对象
* */
function transformjson(id, type, content) {
    if (!content) {
        alert("评论不能为空");
        return;
    }

    //异步刷新
    $.ajax({
        type: "post",
        url: "/comment",
        contentType: "application/json",
        data: JSON.stringify({
            "parentid": id,
            "content": content,
            type: type
        }),
        success: function (response) {
            if (response.code == 200) {
                //如果请求成功，重新加载页面
                window.location.reload();
            } else {
                if (response.code == 2003) {
                    var boolean = confirm(response.message);
                    if (boolean) {
                        window.open("https://github.com/login/oauth/authorize?client_id=8a7e32a4115a4c3cbBEC&redirect_uri=http://localhost:8080/callback&scope=user&state=1");
                        // 保存页面所有的数据，并且除非手动删除，不然不会过期
                        window.localStorage.setItem("closeabel", true);
                    } else {
                        alert(response.message);
                    }
                }
            }
        },
        dataType: "json"
    });
}

/*
* 回复评论功能
* */
function post() {
    //接收前端传来的hidden值
    var questionid = $("#question_id").val();
    var content = $("#comment_content").val();
    transformjson(questionid, 1, content);
}



/*
* 二级评论回复功能
* */
function comment(e) {
    var id = e.getAttribute("data-id");
    var content = $("#input-" + id).val();
    transformjson(id, 2, content)
}

/*
* 二级折叠评论功能
* */
function collapseComments(e) {
    var id = e.getAttribute("data-id");
    var comments = $("#comment-" + id);

    // 获取一下二级评论的展开状态
    var collapse = e.getAttribute("data-collapse");
    if (collapse) {
        // 折叠二级评论
        comments.removeClass("in");
        e.removeAttribute("data-collapse");
        e.classList.remove("active");
    } else {
        var subCommentContainer = $("#comment-" + id);
        if (subCommentContainer.children().length != 1) {
            //展开二级评论
            comments.addClass("in");
            // 标记二级评论展开状态
            e.setAttribute("data-collapse", "in");
            e.classList.add("active");
        } else {
            $.getJSON("/comment/" + id, function (data) {
                // console.log(data);
                $.each(data.date.reverse(), function (index, comment) {
                    var mediaLeftElement = $("<div/>", {
                        "class": "media-left"
                    }).append($("<img/>", {
                        "class": "media-object img-rounded",
                        "src": comment.user.avatarUrl
                    }));

                    var mediaBodyElement = $("<div/>", {
                        "class": "media-body"
                    }).append($("<h5/>", {
                        "class": "media-heading",
                        "html": comment.user.name
                    })).append($("<div/>", {
                        "html": comment.content
                    })).append($("<div/>", {
                        "class": "menu"
                    }).append($("<span/>", {
                        "class": "pull-right",
                        "html": moment(comment.gmtCreate).format('YYYY-MM-DD')
                    })));

                    var mediaElement = $("<div/>", {
                        "class": "media"
                    }).append(mediaLeftElement).append(mediaBodyElement);

                    var commentElement = $("<div/>", {
                        "class": "col-lg-12 col-md-12 col-sm-12 col-xs-12 comments"
                    }).append(mediaElement);

                    subCommentContainer.prepend(commentElement);
                });
                //展开二级评论
                comments.addClass("in");
                // 标记二级评论展开状态
                e.setAttribute("data-collapse", "in");
                e.classList.add("active");
            });
        }
    }
}

//展示标签页
function showSelectTag() {
    $("#select-tag").show();
}

//标签功能
function selectTag(e) {
    var value = e.getAttribute("data-tag");
    var previous = $("#tag").val();
    if (previous.indexOf(value) == -1) {
        if (previous) {
            $("#tag").val(previous + ',' + value);
        } else {
            $("#tag").val(value);
        }
    }
}


//点赞功能
function changecolor() {
    var yes = document.getElementById("yes");
    yes.className = 'color glyphicon glyphicon-thumbs-up icon';
    var yeses = $("#likecount").val();
    var id = $("#commentid").val();
    if (id != null && id != 0) {
        $.post(
            "/likecount", {yeses: yeses, id: id}, function (data) {
            //收到数据
            var d = eval("(" + data + ")");
            // console.log(d);
            $("#commentLikeCount").empty();
            $("#commentLikeCount").append(data);
        })
    }
}

//后台——删除文章
function deleteMes(e){
    var questionid=e.getAttribute("data-id");
    // console.log(questionid);
    $.get(
       "/deleteMsg",{id:questionid},function(data){
           if(data=="success"){
               window.location.reload();
           }
        }
    )
}


//后台——删除用户
function deleteuser(e){
    var userid=e.getAttribute("data-id");
    console.log(userid);
    $.get(
        "/deleteUser",{id:userid},function(data){
            if(data=="success"){
                window.location.reload();
            }
        }
    )
}

//后台——禁用用户
function disableuser(e) {
    var userid=e.getAttribute("data-id");
    console.log(userid);
    $.get(
        "/disableuser",{id:userid},function(data){
            if(data=="success"){
                window.location.reload();
            }
        }
    )
}

//后台——解禁用户
function ableuser(e) {
    var userid=e.getAttribute("data-id");
    console.log(userid);
    $.get(
        "/ableuser",{id:userid},function(data){
            if(data=="success"){
                window.location.reload();
            }
        }
    )
}

//后台——删除评论
function delCom(e){
    var comid = e.getAttribute("data-id");
    console.log(comid);
    $.get(
        "/delCom",{id:comid},function(data){
            if(data=="success"){
                window.location.reload();
            }
        }
    )
}










