<%@ page pageEncoding="UTF-8" %>
<c:set var="ctx" value="${pageContext.request.contextPath}"/>
<c:set var="bound" value="${not empty item ? item : draft}"/>
<form id="itemForm" class="stack-form" method="post" action="${ctx}/item">
    <input type="hidden" name="action" value="${formAction}">
    <c:if test="${formAction == 'update'}">
        <input type="hidden" name="id" value="${bound.itemId}">
    </c:if>
    <label class="field">
        <span>标题</span>
        <input name="title" maxlength="50" required placeholder="例如：蓝色校园卡" value="<c:out value='${bound.title}'/>">
    </label>
    <fieldset class="type-picks">
        <legend>类型</legend>
        <label><input type="radio" name="itemType" value="丢失" ${bound.itemType != '拾到' ? 'checked' : ''}> 丢失</label>
        <label><input type="radio" name="itemType" value="拾到" ${bound.itemType == '拾到' ? 'checked' : ''}> 拾到</label>
    </fieldset>
    <label class="field">
        <span>地点（必填）</span>
        <input name="location" maxlength="100" placeholder="例如：李白河校区图书馆一楼服务台" value="<c:out value='${bound.location}'/>">
    </label>
    <label class="field">
        <span>联系方式（选填）</span>
        <input name="contact" maxlength="50" placeholder="手机 / 微信 / QQ，拾到物品可以不填" value="<c:out value='${bound.contact}'/>">
    </label>
    <label class="field">
        <span>详情（必填）</span>
        <textarea name="description" maxlength="500" rows="6" placeholder="颜色、品牌、特征。拾到的同学请写明物品现在放在哪里"><c:out value="${bound.description}"/></textarea>
    </label>
    <div class="form-actions">
        <button class="btn btn-green" type="submit">${formAction == 'update' ? '保存修改' : '发布信息'}</button>
        <a class="btn btn-ghost" href="${ctx}/item?action=${formAction == 'update' ? 'my' : 'list'}">取消</a>
    </div>
</form>
<script>
/* 地点和详情不使用浏览器自带的 required，这样漏填时弹出统一的警告。 */
(function () {
    var form = document.getElementById("itemForm");
    if (!form) {
        return;
    }
    form.addEventListener("submit", function (event) {
        var location = form.location.value.trim();
        var description = form.description.value.trim();
        if (!location || !description) {
            event.preventDefault();
            alert("地点和详情为必填项。请填写具体地点与详情后再发布。");
        }
    });
})();
</script>
