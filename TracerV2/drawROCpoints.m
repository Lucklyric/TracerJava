%draw ROC points
function drawROCpoints(FPRarray,SEarray)
    figure;
    plot([0 1],[0 1]),axis([0 1 0 1]),title('ROC');
    hold on;
    plot(FPRarray,SEarray,'s'),axis([0 1 0 1]);
end
