package com.emao.swoft.psf.provider;

import com.emao.swoft.psf.BeanUtil;
import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.util.PsiNavigateUtil;
import com.jetbrains.php.lang.psi.elements.Method;
import com.jetbrains.php.lang.psi.elements.PhpPsiElement;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class MarkerProvider implements LineMarkerProvider {
    @Override
    public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        if (!(element instanceof PhpPsiElement)) {
            return null;
        }

        if (! BeanUtil.isPsfCallElement(element)) {
            return null;
        }

        PsiElement destEl = BeanUtil.getPsfCallElement(element);
        if (!(destEl instanceof Method)) {
            return null;
        }
        Method destElMethod = (Method) BeanUtil.getPsfCallElement(element);
        PsiElement markerEl = element.getFirstChild().getFirstChild();

        return new LineMarkerInfo<>(
                markerEl,
                element.getTextRange(),
                AllIcons.Actions.FindEntireFile,
                phpPsiElement -> "To Psf Data",
                (e, elt) -> {
                    PsiNavigateUtil.navigate(destElMethod);
                },
                GutterIconRenderer.Alignment.RIGHT,
                (Supplier<String>) () -> "psf"
        );
    }
}
