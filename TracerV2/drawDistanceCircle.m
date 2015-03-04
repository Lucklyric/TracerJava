%Function For Draw Distance Circle
function drawDistanceCircle(array1,array2,array3,array4,array5,array6)
figure
subplot(3,2,1),drawFigure(array4),title('B-A(Base)');
subplot(3,2,2),drawFigure(array5),title('C-A(Base)');
subplot(3,2,3),drawFigure(array1),title('A-B(Base)');
subplot(3,2,4),drawFigure(array6),title('C-B(Base)');
subplot(3,2,5),drawFigure(array2),title('A-C(Base)');
subplot(3,2,6),drawFigure(array3),title('B-C(Base)');
    function drawFigure(array)
        normalAngle = 360.0/(numel(array));
        plotX = zeros(1,numel(array));
        plotY = zeros(1,numel(array));
        for i=1:numel(array)
            plotX(i) = (cosd(i*normalAngle))*array(i);
            plotY(i) = (sind(i*normalAngle))*array(i);
        end
        PlotAxisAtOrigin(plotX,plotY);
    end
end

