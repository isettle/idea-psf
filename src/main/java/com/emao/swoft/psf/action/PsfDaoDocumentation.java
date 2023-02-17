package com.emao.swoft.psf.action;

import com.emao.swoft.psf.BeanUtil;
import com.intellij.psi.PsiElement;
import com.jetbrains.php.PhpIndex;
import com.jetbrains.php.lang.documentation.PhpDocumentationProvider;
import com.jetbrains.php.lang.psi.elements.*;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Objects;

public class PsfDaoDocumentation extends PhpDocumentationProvider {
    @Override
    public @Nullable @Nls String generateDoc(@NotNull PsiElement element, @Nullable PsiElement originalElement) {
        if (!(element instanceof PhpPsiElement)) {
            return null;
        }
        if (!(element.getParent() instanceof PhpClass)) {
            return null;
        }

        PhpClass psfClass = (PhpClass)element.getParent();
        if (!psfClass.getName().equals("Psf")) {
            return super.generateDoc(element, originalElement);
        }

        assert originalElement != null;
        MethodReference methodRefer = (MethodReference) originalElement.getParent();
        if (methodRefer == null) {
            return super.generateDoc(element, originalElement);
        }

        Method destEl = (Method) BeanUtil.getPsfCallElement(methodRefer);
        String methodDoc = super.generateDoc(destEl, destEl.getNameIdentifier());
        @Nullable Field destDaoEl = Objects.requireNonNull(destEl.getContainingClass()).findFieldByName("dao", false);
        String tips = "<div class=\"definition\">Data层说明</div>" + methodDoc;
        if (destDaoEl == null){
            return tips;
        }

        @NotNull PhpIndex phpIndex = PhpIndex.getInstance(element.getProject());
        String daoFQN = destDaoEl.getDocType().toString();
        Collection<PhpClass> daoPhpClasses = phpIndex.getClassesByFQN(destDaoEl.getDocType().toString());
        if (daoPhpClasses.size() == 0) {
            return tips;
        }

        // Dao php Class
        PhpClass daoPhpClass = daoPhpClasses.iterator().next();
        String daoMethodAnchor = "";
        @Nullable Method daoMethod = daoPhpClass.findMethodByName(Objects.requireNonNull(destEl.getNameIdentifier()).getText());
        if (daoMethod != null) {
            String daoMethodFQN = daoFQN + "::" + daoMethod.getName();
            daoMethodAnchor = "psi_element://" + daoMethodFQN;
        }
        assert daoMethod != null;
        String daoMethodContent = daoMethod.getText();
        tips += "<hr><br/><div class=\"definition\">Dao层说明</div>\n" +
                "<div class='definition'>\n" +
                "<pre><b>Dao</b> <a href='" + daoMethodAnchor + "'>" + daoFQN + "</a></pre>\n" +
                "</div>" +
                "<div class='content'><textarea rows=\"" + daoMethodContent.split("\n").length / 2  + "\" cols=\"" + daoMethodContent.indexOf("\n") + "\">" + daoMethod.getText() + "</textarea></div>\n";

        // Entity
        @Nullable Field entityField = daoPhpClass.findOwnFieldByName("entity", false);
        if (entityField == null) {
            return tips;
        }
        Collection<PhpClass> entityPhpClasses = phpIndex.getClassesByFQN(entityField.getDocType().toString());
        if (entityPhpClasses.size() == 0) {
            return tips;
        }
        PhpClass entityPhpClass = entityPhpClasses.iterator().next();
        tips += "<hr/><br/><div class=\"definition\">Entity层说明</div>\n" +
                "<div class='definition'>\n" +
                "    <div style=\"white-space: pre-wrap\">" + Objects.requireNonNull(entityPhpClass.getDocComment()).getText().replaceAll("\n", "<br/>") + "</div>\n" +
                "    <pre><b>Entity</b> <a href='psi_element://" + entityPhpClass.getFQN() + "::setId'>" + entityPhpClass.getFQN() + "</a></pre>\n" +
                "</div>";
        return tips;
    }
}
