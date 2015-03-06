%compare Area
function percentage = compareArea(array1,array2)
    %a1 = polyarea(array1(:,1),array1(:,2));
    [~,a1] = convexHull(DelaunayTri(array1));
    %a2 = polyarea(array2(:,1),array2(:,2));
    [~,a2] = convexHull(DelaunayTri(array2));
    percentage = abs(a1-a2)/a2;
end