%draw ROC points
function drawROCpoints(FPRarray,SEarray)
    figure;
    plot([0 1],[0 1]),axis([0 1 0 1]),title('C-AB');
    xlabel('WeightedSpecificity') % x-axis label
    ylabel('WeightedSensitivity') % y-axis label
    hold on;
    plot(FPRarray,SEarray,'s'),axis([0 1 0 1]);
end
