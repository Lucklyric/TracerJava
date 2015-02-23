%TP FP TN FN Defnintion
function [positiveAB,positiveAC,positiveBC] = positiveNegationDefV1(pointsA,pointsB,pointsC,array1,array2,array3,array4,array5,array6)
    %getABset
    positiveAB = [];
    negativeAB = [];
    positiveAC = [];
    negativeAC = [];
    positiveBC = [];
    negativeBC = [];
    for i = 1 : numel(array1)
        if array1(i) <= 3
            positiveAB(end+1,:) = pointsB(i,:);
        else
            negativeAB(end+1,:) = pointsB(i,:);
        end
    end
    for i = 1 : numel(array4)
        if array4(i) <= 3
            positiveAB(end+1,:) = pointsA(i,:);
        else
            negativeAB(end+1,:) = pointsA(i,:);
        end
    end
    
    %getACset
    for i = 1 : numel(array2)
        if array2(i) <= 3
            positiveAC(end+1,:) = pointsC(i,:);
        else
            negativeAC(end+1,:) = pointsC(i,:);
        end
    end
    for i = 1 : numel(array5)
        if array5(i) <= 3
            positiveAC(end+1,:) = pointsA(i,:);
        else
            negativeAC(end+1,:) = pointsA(i,:);
        end
    end
    
    %getBCset
    for i = 1 : numel(array3)
        if array3(i) <= 3
            positiveBC(end+1,:) = pointsC(i,:);
        else
            negativeBC(end+1,:) = pointsC(i,:);
        end
    end
    for i = 1 : numel(array6)
        if array6(i) <= 3
            positiveBC(end+1,:) = pointsB(i,:);
        else
            negativeBC(end+1,:) = pointsB(i,:);
        end
    end
    
    positiveAB = unique(positiveAB,'rows');
    positiveAC = unique(positiveAC,'rows');
    positiveBC = unique(positiveBC,'rows');
end